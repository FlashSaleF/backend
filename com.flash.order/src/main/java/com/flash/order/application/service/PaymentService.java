package com.flash.order.application.service;

import com.flash.order.application.dtos.request.PaymentCallbackDto;
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

    public IamportResponse<Payment> processPayment(PaymentCallbackDto request) {
        try {
            // 주문 내역 조회
            Order order = orderRepository.findOrderAndPayment(request.orderUid())
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
}
