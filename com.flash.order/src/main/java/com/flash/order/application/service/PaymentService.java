package com.flash.order.application.service;

import com.flash.base.exception.CustomException;
import com.flash.order.application.dtos.mapper.PaymentMapper;
import com.flash.order.application.dtos.request.PaymentCallbackDto;
import com.flash.order.application.dtos.request.ProductStockDecreaseRequestDto;
import com.flash.order.application.dtos.response.PaymentDetailsResponseDto;
import com.flash.order.application.dtos.response.PaymentResponseDto;
import com.flash.order.application.dtos.response.RefundResponseDto;
import com.flash.order.domain.exception.OrderErrorCode;
import com.flash.order.domain.exception.PaymentErrorCode;
import com.flash.order.domain.model.Order;
import com.flash.order.domain.model.OrderStatus;
import com.flash.order.domain.model.PaymentStatus;
import com.flash.order.domain.repository.OrderRepository;
import com.flash.order.domain.repository.PaymentRepository;
import com.flash.order.infrastructure.messaging.MessagingProducerService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;
    private final PaymentMapper paymentMapper;
    private final MessagingProducerService messagingProducerService;

    @Transactional
    public IamportResponse<Payment> processPayment(PaymentCallbackDto request) {
        try {
            // 주문 내역 조회
            Order order = orderRepository.findByIdAndIsDeletedFalse(request.orderId())
                    .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

            // 결제 단건 조회 (아임포트)
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.paymentUid());

            // 결제 완료 상태 확인
            if (!"paid".equals(iamportResponse.getResponse().getStatus())) {
                handleFailedPayment(order);
                throw new CustomException(PaymentErrorCode.PAYMENT_NOT_COMPLETED);
            }

            // DB에 저장된 결제 금액
            int savedPaymentPrice = order.getPayment().getPrice();
            // 실제 결제된 금액
            int iamportPaidAmount = iamportResponse.getResponse().getAmount().intValue();

            // 결제 금액 검증
            if (iamportPaidAmount != savedPaymentPrice) {
                handleInvalidPaymentAmount(order, iamportResponse.getResponse().getImpUid(), iamportPaidAmount);
                throw new CustomException(PaymentErrorCode.INVALID_PAYMENT_AMOUNT);
            }

            // 결제 성공 처리: 이때 결제 완료되면 생성된 paymentUid 할당해줌.
            order.getPayment().changePaymentBySuccess(PaymentStatus.completed, iamportResponse.getResponse().getImpUid());
//            paymentRepository.save(order.getPayment()); // 결제 내역 저장
            Order savedOrder = orderRepository.save(order); // 주문 상태 저장


            messagingProducerService.sendPaymentRequest(savedOrder);

            return iamportResponse;

        } catch (IamportResponseException | IOException e) {
            log.error("아임포트 응답 오류: {}", e.getMessage());
            throw new CustomException(PaymentErrorCode.PAYMENT_PROCESSING_ERROR);
        }
    }

    // 결제가 실패했을 경우 처리
    private void handleFailedPayment(Order order) {
        orderRepository.delete(order); // 주문 삭제
        paymentRepository.delete(order.getPayment()); // 결제 내역 삭제
    }

    // 결제 금액 위변조 의심 시 처리
    private void handleInvalidPaymentAmount(Order order, String impUid, int paidAmount) throws IamportResponseException, IOException {
        // 주문 및 결제 삭제
        orderRepository.delete(order);
        paymentRepository.delete(order.getPayment());

        // 위변조 의심 결제 취소 (아임포트 API)
        CancelData cancelData = new CancelData(impUid, true, new BigDecimal(paidAmount));
        iamportClient.cancelPaymentByImpUid(cancelData);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDto> getAllPayments(Pageable pageable) {
        Page<com.flash.order.domain.model.Payment> payments = paymentRepository.findAllByIsDeletedFalse(pageable);

        return payments.map(paymentMapper::convertToResponseDto);
    }

    //결제 조회
    public PaymentDetailsResponseDto getPaymentDetails(UUID paymentId) {
        try {
            Long currentUserId = Long.valueOf(getCurrentUserId());
            String authority = getCurrentUserAuthority();

            com.flash.order.domain.model.Payment payment = paymentRepository.findByIdAndIsDeletedFalse(paymentId)
                    .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_HISTORY_NOT_FOUND));

            // 권한이 ROLE_MASTER가 아닌 경우에만 주문자가 맞는지 확인
            if (!authority.equals("ROLE_MASTER") && !payment.getUserId().equals(currentUserId)) {
                throw new CustomException(OrderErrorCode.INVALID_PERMISSION_REQUEST);
            }

            String paymentUid = payment.getPaymentUid();

            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(paymentUid);

            if (iamportResponse.getResponse() == null) {
                throw new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND);
            }

            // 조회한 Payment 엔티티를 PaymentResponseDto로 변환
            return paymentMapper.convertToDetailsResponseDto(iamportResponse.getResponse());

        } catch (IamportResponseException | IOException e) {
            throw new CustomException(PaymentErrorCode.PAYMENT_RETRIEVAL_ERROR);
        }
    }

    //실제 결제 취소
    @Transactional
    public RefundResponseDto refundPayment(String paymentUid) {
        // 결제 내역 조회
        com.flash.order.domain.model.Payment payment = paymentRepository.findByPaymentUid(paymentUid)
                .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_HISTORY_NOT_FOUND));

        try {
            // 아임포트 API를 이용한 환불 요청
            CancelData cancelData = new CancelData(payment.getPaymentUid(), true, new BigDecimal(payment.getPrice()));
            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(cancelData);

            if ("cancelled".equals(response.getResponse().getStatus())) {
                // 환불 성공 처리: 필요시 결제 상태 업데이트
                payment.changePaymentByCancell(PaymentStatus.cancelled, response.getResponse().getImpUid());
                payment.delete();

                Order order = payment.getOrder();
                order.changeOrderStatus(OrderStatus.cancelled);
                order.delete();

            } else {
                throw new CustomException(PaymentErrorCode.REFUND_FAILED);
            }

            return paymentMapper.convertToRefundResponseDto(response.getResponse());

        } catch (IamportResponseException | IOException e) {
            throw new CustomException(PaymentErrorCode.REFUND_PROCESSING_ERROR);
        }
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String getCurrentUserAuthority() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new CustomException(OrderErrorCode.INVALID_PERMISSION_REQUEST))
                .getAuthority();
    }

}
