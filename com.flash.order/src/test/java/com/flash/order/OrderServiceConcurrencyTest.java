package com.flash.order;

import com.flash.order.application.dtos.response.ProductResponseDto;
import com.flash.order.application.service.OrderService;
import com.flash.order.infrastructure.client.ProductFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(locations = "classpath:.env")
public class OrderServiceConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceConcurrencyTest.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductFeignClient productFeignClient;

    private UUID orderId;
    private UUID productId;
    private int initialStock; // 실제 남은 재고를 저장

    @BeforeEach
    public void setup() {
        // 테스트를 위한 상품 및 주문 생성
        productId = UUID.fromString("2326ec16-6e9f-4188-b9ef-406e11ac3f64");
        orderId = UUID.fromString("1d707d9d-99d6-497a-826f-4be95a728e19");

        // 외부에서 실제 재고 정보를 가져옴
        ProductResponseDto product = productFeignClient.getProduct(productId);
//        assertThat(product).isNotNull();

        // 실제 재고 값을 저장
        initialStock = product.stock();
        System.out.println("Initial stock: " + initialStock);

        // 재고가 충분히 있는지 확인 (테스트를 위해 최소 100 이상이어야 함)
        assertThat(initialStock).isGreaterThanOrEqualTo(100);
    }

    @Test
    public void testConcurrentStockDecrease() throws Exception {
        int numThreads = 10; // 사용할 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    orderService.handlePaymentCompleted(orderId);
                } catch (Exception e) {
                    log.error("Error processing payment: {}", e.getMessage());
                    // 예외 발생 시, 테스트를 실패하도록 처리할 수 있습니다.
                    // Assert.fail("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 끝날 때까지 대기

        // ExecutorService 종료
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // 외부 서비스에서 재고 정보 가져오기
        ProductResponseDto product = productFeignClient.getProduct(productId);

        // 예상 재고 계산 (초기 재고에서 10 감소한 값)
        int expectedStock = initialStock - numThreads;
        System.out.println("Expected stock: " + expectedStock);

        // 재고 감소가 예상대로 일어났는지 확인
        assertThat(product.stock()).isEqualTo(expectedStock);
    }
}
