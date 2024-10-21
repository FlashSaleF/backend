package com.flash.alarm.application.service;

import com.flash.alarm.domain.model.Alarm;
import com.flash.alarm.domain.repository.AlarmRepository;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("alarm-test")
@TestPropertySource(locations = "/test.env")
public class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private JavaMailSender mailSender; // JavaMailSender Mock 추가

    @Mock
    private SpringTemplateEngine templateEngine; // SpringTemplateEngine Mock 추가

    @Mock
    private RetryTemplate retryTemplate;

    @Mock
    private LockProvider lockProvider;

    @InjectMocks
    private AlarmService alarmService;

    @Test
    @DisplayName("멀티 스레드 환경에서의 ShedLock 락 테스트")
    public void testSendFlashSaleNotificationWithLockInMultiThread() throws InterruptedException {
        // Given: 특정 상품에 대한 알람과 락이 설정되었을 때
        UUID productId = UUID.randomUUID();

        // 알람 데이터 설정
        Alarm alarm = Alarm.builder()
                .id(UUID.randomUUID())
                .title("Flash Sale Alert")
                .content("Your product is on sale!")
                .userId(1L)
                .userEmail("test@example.com")
                .flashSaleProductId(productId)
                .flashSaleId(UUID.randomUUID())
                .flashSaleProductName("Test Product")
                .build();

        // 알람 레포지토리에서 특정 상품의 알람을 반환하도록 Mock 설정
        when(alarmRepository.findAllByProductId(productId)).thenReturn(List.of(alarm));

        // Mock된 Lock 객체 설정
        SimpleLock lock = mock(SimpleLock.class);
        // LockConfiguration 생성 시 lockAtMostFor와 lockAtLeastFor 추가
        LockConfiguration lockConfiguration = new LockConfiguration(
                Instant.now(),
                "sendMailLock_" + productId,
                Duration.ofMinutes(10), // lockAtMostFor
                Duration.ofMinutes(5) // lockAtLeastFor
        );
        when(lockProvider.lock(eq(lockConfiguration))).thenReturn(Optional.of(lock));

        // 락을 해제하는 메서드 설정
        doNothing().when(lock).unlock();

        // 스레드 수 설정 (멀티 스레드 환경)
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When: 여러 스레드에서 동시에 메일 발송 메서드가 호출될 때
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    alarmService.sendFlashSaleNotification(productId);
                } finally {
                    latch.countDown();  // 스레드가 끝나면 카운트다운
                }
            });
        }

        // 모든 스레드가 작업을 끝날 때까지 대기
        latch.await();

        // Then: 알람 레포지토리가 단 한 번만 호출되었는지 확인 (락이 걸려서)
        verify(alarmRepository, times(1)).findAllByProductId(productId);

        // 락 해제 여부 확인
        verify(lock, times(1)).unlock();

        executorService.shutdown(); // ExecutorService 종료
    }


    @Test
    public void testSendFlashSaleNotificationWithLockSingleThread() {
        UUID productId = UUID.randomUUID();

        // Mock 설정: 알람 리스트 반환
        Alarm alarm = Alarm.builder()
                .id(UUID.randomUUID())
                .title("Flash Sale Alert")
                .content("Your product is on sale!")
                .userId(1L)
                .userEmail("test@example.com")
                .flashSaleProductId(productId)
                .flashSaleId(UUID.randomUUID())
                .flashSaleProductName("Test Product")
                .build();

        // Mock 설정: 알람 리스트 반환
        when(alarmRepository.findAllByProductId(productId)).thenReturn(List.of(alarm));

        // Mock 설정: 템플릿 처리 결과 반환
        when(templateEngine.process(eq("alarm"), any())).thenReturn("<html>...</html>");

        // Mock 설정: RetryTemplate의 execute 메서드가 정상적으로 호출되도록 설정
        doAnswer(invocation -> {
            // 첫 번째 인수는 RetryCallback이고, 그 내부에서 null을 반환
            return null;
        }).when(retryTemplate).execute(any());

        // 메서드 호출 (단일 스레드)
        alarmService.sendFlashSaleNotification(productId);

        // 검증
        verify(alarmRepository, times(1)).findAllByProductId(productId); // 메서드는 한 번만 호출되어야 함
        verify(templateEngine, times(1)).process(eq("alarm"), any()); // 템플릿 처리 호출 검증
        verify(retryTemplate, times(1)).execute(any()); // RetryTemplate의 execute 호출 검증
    }
}
