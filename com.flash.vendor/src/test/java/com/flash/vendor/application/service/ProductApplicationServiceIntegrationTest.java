package com.flash.vendor.application.service;

import com.flash.vendor.application.dto.request.ProductStockDecreaseRequestDto;
import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import com.flash.vendor.domain.repository.ProductRepository;
import com.flash.vendor.infrastructure.repository.JpaProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")  // 테스트 프로파일 사용
@TestPropertySource(locations = "/vendor.env")
public class ProductApplicationServiceIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(ProductApplicationServiceTest.class);
    @Autowired
    private ProductApplicationService productApplicationService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JpaProductRepository jpaProductRepository;
    private Product savedProduct;

    @BeforeEach
    public void setUp() {
        // 테스트에 필요한 공통 데이터 설정
        Product product = new Product(
                UUID.randomUUID(),
                "transactional-test-product",
                9900,
                100,  // 초기 재고
                ProductStatus.AVAILABLE,
                UUID.randomUUID(),
                "transactional-test-product-description"
        );
        savedProduct = productRepository.save(product);
    }

    @AfterEach
    public void tearDown() {
        // 테스트가 끝난 후 데이터 삭제
        jpaProductRepository.delete(savedProduct);  // 저장된 상품 삭제
        jpaProductRepository.flush();  // 즉시 데이터베이스에 반영
    }

    @Test
    @DisplayName("재고 1개 감소 통합 테스트")
    public void testDecreaseProductStock() {

        ProductStockDecreaseRequestDto request = new ProductStockDecreaseRequestDto(1);

        // 재고 감소 로직 실행
        productApplicationService.decreaseProductStock(savedProduct.getId(), request);

        // 데이터베이스에서 상품 다시 조회
        Product updatedProduct = productRepository.findByIdAndIsDeletedFalse(savedProduct.getId());

        // 재고가 99로 감소했는지 검증
        assertEquals(99, updatedProduct.getStock());
    }

    @Test
    @DisplayName("동시 재고 10개 감소 통합 테스트")
    public void testDecreaseProductStockConcurrently() throws InterruptedException {

        ProductStockDecreaseRequestDto request = new ProductStockDecreaseRequestDto(1);
        int threadCount = 10;  // 동시에 실행할 스레드 수

        // 멀티 스레드로 재고 감소 테스트
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 여러 스레드가 동시에 재고 감소 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    Product beforeProduct = productRepository.findByIdAndIsDeletedFalse(savedProduct.getId());
                    logger.info("Thread: {} | Current Stock: {} | Decreasing by: {}", Thread.currentThread().getName(), beforeProduct.getStock(), request.quantity());

                    productApplicationService.decreaseProductStock(savedProduct.getId(), request);

                    Product updatedProduct = productRepository.findByIdAndIsDeletedFalse(savedProduct.getId());
                    logger.info("Thread: {} | Updated Stock: {}", Thread.currentThread().getName(), updatedProduct.getStock());
                } finally {
                    latch.countDown();  // 스레드가 끝나면 latch 감소
                }
            });
        }

        // 모든 스레드가 종료될 때까지 대기
        latch.await();

        // 데이터베이스에서 상품 다시 조회
        Product updatedProduct = productRepository.findByIdAndIsDeletedFalse(savedProduct.getId());

        // 모든 스레드가 재고 감소했는지 검증 (100 - 10 = 90)
        assertEquals(90, updatedProduct.getStock());
    }

}

