package com.flash.order.presentation.controller;

import com.flash.order.application.dtos.request.PaymentCallbackDto;
import com.flash.order.application.dtos.response.PaymentDetailsResponseDto;
import com.flash.order.application.dtos.response.PaymentResponseDto;
import com.flash.order.application.dtos.response.RefundResponseDto;
import com.flash.order.application.service.PaymentService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    //결제 검증 및 처리
    @PostMapping("/process")
    public ResponseEntity<IamportResponse<Payment>> processPayment(
            @Valid @RequestBody PaymentCallbackDto request
    ) {
        try {
            // 결제 처리 및 검증 로직 수행(검증 실패시 db서 삭제)
            IamportResponse<Payment> paymentResponse = paymentService.processPayment(request);

            // 결제 완료 응답 반환
            return ResponseEntity.ok(paymentResponse);
        } catch (IllegalArgumentException e) {
            // 주문을 찾지 못했을 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (RuntimeException e) {
            // 결제 오류 발생 시
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    //결제 조회
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPayment(@PathVariable UUID paymentId) {
        PaymentResponseDto payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    //결제 전체 조회
    @GetMapping
    public ResponseEntity<Page<PaymentResponseDto>> getAllPayments(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PaymentResponseDto> payments = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(payments);
    }


//    //실제 결제 조회
//    @GetMapping("/{paymentUid}")
//    public ResponseEntity<PaymentDetailsResponseDto> getPaymentDetails(@PathVariable String paymentUid) {
//        PaymentDetailsResponseDto paymentDetails = paymentService.getPaymentDetails(paymentUid);
//        return ResponseEntity.ok(paymentDetails);
//    }
//
//    //실제 결제 취소
//    @PostMapping("/refund/{paymentUid}")
//    public ResponseEntity<RefundResponseDto> refundPayment(@PathVariable String paymentUid) {
//        RefundResponseDto response = paymentService.refundPayment(paymentUid);
//        return ResponseEntity.ok(response);
//    }

}
