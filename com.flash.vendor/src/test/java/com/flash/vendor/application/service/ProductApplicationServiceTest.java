package com.flash.vendor.application.service;

import com.flash.vendor.application.dto.request.ProductStockDecreaseRequestDto;
import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import com.flash.vendor.domain.service.ProductService;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "/vendor.env")
class ProductApplicationServiceTest {

    @MockBean
    private EurekaClient eurekaClient;
    @MockBean
    private EurekaClientConfig eurekaClientConfig;
    @MockBean
    private RedisLockService redisLockService;
    @MockBean
    private ProductService productService;

    @Autowired
    private ProductApplicationService productApplicationService;

    @Test
    @DisplayName("재고감소 테스트")
    public void testDecreaseProductStock() {
        Product product = new Product(
                UUID.randomUUID(),
                "test-product",
                9900,
                100,
                ProductStatus.AVAILABLE,
                UUID.randomUUID(),
                "test-product-description"
        );
        ProductStockDecreaseRequestDto request = new ProductStockDecreaseRequestDto(1);

        // Mock 설정: Redis 락 획득 성공 시뮬레이션
        doAnswer(invocation -> {
            Callable<Product> task = invocation.getArgument(1);
            return task.call();  // 락이 성공적으로 획득되면 액션 실행
        }).when(redisLockService).lockAndExecute(anyString(), ArgumentMatchers.<Callable<Product>>any());

        // Mock 설정: 재고 감소 동작 (현재 재고에서 감소)
        doAnswer(invocation -> {
            product.decreaseProductStock(1);
            return product;
        }).when(productService).decreaseStock(product.getId(), 1);

        // 메서드 호출 및 검증
        productApplicationService.decreaseProductStock(product.getId(), request);

        // 재고 감소 메서드 호출 여부 확인
        verify(productService, times(1)).decreaseStock(product.getId(), 1);
        assertEquals(99, product.getStock());
    }

    @Test
    @DisplayName("동시재고감소 테스트")
    public void testDecreaseProductStockConcurrently() throws InterruptedException {
        UUID productId = UUID.randomUUID();
        ProductStockDecreaseRequestDto request = new ProductStockDecreaseRequestDto(1);
        int initialStock = 100;  // 초기 재고
        AtomicInteger currentStock = new AtomicInteger(initialStock);  // 스레드 안전한 재고 값
        int threadCount = 10;  // 동시에 실행할 스레드 수

        // Mock 설정: Redis 락 획득 성공 시뮬레이션
        doAnswer(invocation -> {
            Callable<Product> task = invocation.getArgument(1);
            return task.call(); // 락이 성공적으로 획득되면 액션 실행
        }).when(redisLockService).lockAndExecute(anyString(), ArgumentMatchers.<Callable<Product>>any());

        // Mock 설정: 재고 감소 동작 (현재 재고에서 감소)
        doAnswer(invocation -> {
            int quantity = invocation.getArgument(1);
            currentStock.addAndGet(-quantity);  // 재고를 1 감소
            return new Product(
                    productId,
                    "test-product",
                    9900,
                    currentStock.get(),  // 현재 재고로 업데이트된 상품 객체 생성
                    ProductStatus.AVAILABLE,
                    UUID.randomUUID(),
                    "test-product-description"
            );
        }).when(productService).decreaseStock(any(UUID.class), anyInt());

        // 멀티 스레드로 재고 감소 테스트
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 여러 스레드가 동시에 재고 감소 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productApplicationService.decreaseProductStock(productId, request);
                } finally {
                    latch.countDown();  // 스레드가 끝나면 latch 감소
                }
            });
        }

        // 모든 스레드가 종료될 때까지 대기
        latch.await();

        // 재고 감소 메서드 호출 여부 확인
        verify(productService, times(threadCount)).decreaseStock(productId, request.quantity());
        assertEquals(initialStock - threadCount, currentStock.get());
    }
}
