package com.flash.order.application.service;

import com.flash.order.application.dtos.mapper.PaymentMapper;
import com.flash.order.application.dtos.request.PaymentCallbackDto;
import com.flash.order.application.dtos.response.PaymentResponseDto;
import com.flash.order.application.dtos.response.RefundResponseDto;
import com.flash.order.domain.model.Order;
//import com.flash.order.domain.model.Payment;
import com.flash.order.domain.model.PaymentStatus;
import com.flash.order.domain.repository.OrderRepository;
import com.flash.order.domain.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;
    private final PaymentMapper paymentMapper;

    public IamportResponse<Payment> processPayment(PaymentCallbackDto request) {
        try {
            // 주문 내역 조회
            Order order = orderRepository.findOrderByOrderUid(request.orderUid())
                    .orElseThrow(() -> new IllegalArgumentException("해당 주문 내역을 찾을 수 없습니다."));

            // 결제 단건 조회 (아임포트)
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.paymentUid());

            // 결제 완료 상태 확인
            if (!"paid".equals(iamportResponse.getResponse().getStatus())) {
                handleFailedPayment(order);
                throw new RuntimeException("결제가 완료되지 않았습니다.");
            }

            // DB에 저장된 결제 금액
            int savedPaymentPrice = order.getPayment().getPrice();
            // 실제 결제된 금액
            int iamportPaidAmount = iamportResponse.getResponse().getAmount().intValue();

            // 결제 금액 검증
            if (iamportPaidAmount != savedPaymentPrice) {
                handleInvalidPaymentAmount(order, iamportResponse.getResponse().getImpUid(), iamportPaidAmount);
                throw new RuntimeException("결제 금액 위변조가 의심됩니다.");
            }

            // 결제 성공 처리: 이때 결제 완료되면 생성된 paymentUid 할당해줌.
            order.getPayment().changePaymentBySuccess(PaymentStatus.completed, iamportResponse.getResponse().getImpUid());
            orderRepository.save(order); // 주문 상태 저장

            return iamportResponse;

        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException("결제 처리 중 오류 발생", e);
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

    // 실제 결제 조회 메소드
    public PaymentResponseDto getPaymentDetails(String paymentUid) {
        try {
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(paymentUid);

            if (iamportResponse.getResponse() == null) {
                throw new IllegalArgumentException("결제 정보를 찾을 수 없습니다.");
            }

            // 조회한 Payment 엔티티를 PaymentResponseDto로 변환
            return paymentMapper.convertToResponseDto(iamportResponse.getResponse());

        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException("결제 조회 중 오류 발생", e);
        }
    }

    //실제 결제 취소
    @Transactional
    public RefundResponseDto refundPayment(String paymentUid) {
        // 결제 내역 조회
        com.flash.order.domain.model.Payment payment = paymentRepository.findByPaymentUid(paymentUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 내역을 찾을 수 없습니다."));

        try {
            // 아임포트 API를 이용한 환불 요청
            CancelData cancelData = new CancelData(payment.getPaymentUid(), true, new BigDecimal(payment.getPrice()));
            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(cancelData);

            if ("cancelled".equals(response.getResponse().getStatus())) {
                // 환불 성공 처리: 필요시 결제 상태 업데이트
                payment.changePaymentByCancell(PaymentStatus.cancelled, response.getResponse().getImpUid());
                paymentRepository.save(payment); // 환불된 결제 정보 저장
            } else {
                throw new RuntimeException("환불에 실패했습니다.");
            }

            return paymentMapper.convertToRefundResponseDto(response.getResponse());

        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException("환불 처리 중 오류 발생", e);
        }
    }

}
