package com.flash.alarm.application.service;

import com.flash.alarm.application.dto.reqeuest.AlarmRequestDto;
import com.flash.alarm.application.dto.response.AlarmResponseDto;
import com.flash.alarm.application.dto.response.UserResponseDto;
import com.flash.alarm.application.service.util.AlarmAuthService;
import com.flash.alarm.application.service.util.AlarmMapper;
import com.flash.alarm.domain.exception.AlarmErrorCode;
import com.flash.alarm.domain.model.Alarm;
import com.flash.alarm.domain.model.ScheduledTaskEntity;
import com.flash.alarm.domain.model.TaskStatus;
import com.flash.alarm.domain.repository.AlarmRepository;
import com.flash.alarm.domain.repository.ScheduledTaskRepository;
import com.flash.base.exception.CustomException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j(topic = "Alarm Service")
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine; // Thymeleaf 템플릿 엔진을 사용하기 위한 객체
    private final AlarmRepository alarmRepository;
    private final AlarmAuthService alarmAuthService;
    private final RetryTemplate retryTemplate;
    private final ScheduledTaskRepository taskRepository;

    @SchedulerLock(
            name = "sendMailLock_#{#productId}", // 락의 이름을 설정. 중복을 방지할 유니크한 이름.
            lockAtLeastFor = "PT3M", // 최소 3분 동안 락을 유지
            lockAtMostFor = "PT10M"  // 최대 10분 동안 락을 유지
    )
    public Boolean sendFlashSaleNotification(UUID productId) {
        List<Alarm> forEmail = alarmRepository.findAllByProductId(productId);
        // 리스트가 비어 있는 경우 예외 처리
        if (forEmail.isEmpty()) {
            log.info("제품 ID {}에 대한 알람 설정이 없습니다.", productId);
            return false;
        }

        for (Alarm alarm : forEmail) {
            // 메일 제목
            String subject = "플래시 세일 알림 - " + alarm.getFlashSaleProductName();

            // 메일 본문 생성
            Context context = new Context(Locale.KOREAN);
            context.setVariable("productName", alarm.getFlashSaleProductName());
            String htmlContent = templateEngine.process("alarm", context);

            // 메일 전송
            sendEmail(alarm.getUserEmail(), subject, htmlContent);

            // 메일 전송 성공 시 Alarm 객체 업데이트
            alarm.setTitle(subject);
            alarm.setContent(htmlContent);
            alarmRepository.save(alarm);
        }
        return true;
    }

    public void sendEmail(String to, String subject, String htmlContent) {
        retryTemplate.execute(context -> {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                mimeMessageHelper.setFrom("Flash Sale Service <flash-sale@gmail.com>"); // 구글 SMTP서버의 보안 정책으로 이름은 변경되지만, 인증되지 않은 e-mail주소로는 변경되지 않음.
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(htmlContent, true);

                // 메일 전송
                mailSender.send(mimeMessage);
                log.info("메일 전송 성공. 수신자: {}", to);

                return null; // 정상 종료

            } catch (MessagingException e) {
                log.error("메일 전송 중 오류: {}", e.getMessage());
                throw new CustomException(AlarmErrorCode.MAIL_NOT_SENT);
            }
        });
    }


    @Transactional
    public UUID setAlarm(AlarmRequestDto alarmRequestDto) {
        UserResponseDto userResponseDto = alarmAuthService.verifyIdentity();
        Optional<ScheduledTaskEntity> scheduledTaskEntity = taskRepository.findByFlashSaleProductIdAndStatus(alarmRequestDto.flashSaleProductId(), TaskStatus.SCHEDULED);
        if (!scheduledTaskEntity.isPresent()) {
            log.error("존재하지 않는 플래시 세일");
            throw new CustomException(AlarmErrorCode.PRODUCT_NOT_FOUND);
        }
        // Todo: 시간 변경
        // 메일이 전송되는 시간 10분 전부터는 알람 설정 못하도록.
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        if (now.isAfter(scheduledTaskEntity.get().getSchedulingTime().minusMinutes(10))) {
            log.error("알람 시간이 지나거나 임박하여 알림 설정 불가");
            throw new CustomException(AlarmErrorCode.INVALID_TIME);
        }

        Optional<Alarm> isExists = alarmRepository.findByFlashSaleProductIdAndUserId(alarmRequestDto.flashSaleProductId(), userResponseDto.id());
        if (isExists.isPresent()) {
            log.error("이미 알람 설정을 한 상품");
            throw new CustomException(AlarmErrorCode.DUPLICATED_ALARM);
        }
        log.info("email: {}", userResponseDto.email());
        log.info("name: {}", userResponseDto.id());

        return alarmRepository.save(AlarmMapper.entityFrom(alarmRequestDto, userResponseDto)).getId();

    }

    @Transactional(readOnly = true)
    public AlarmResponseDto getAlarm(UUID alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId).orElseThrow(
                () -> {
                    log.error("유효한 알람 내역이 없습니다");
                    throw new CustomException(AlarmErrorCode.ALARM_NOT_FOUND);
                }
        );
        return AlarmMapper.fromEntity(alarm);
    }

    @Transactional(readOnly = true)
    public Page<AlarmResponseDto> getAlarmList(int page, int size, String sortBy, boolean isAsc) {
        // 페이징 처리를 하기 위한 Sort 객체 생성
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        // page, size, Sort객체로 Pageable 객체 완성
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Alarm> all = alarmRepository.findAll(pageable);
        if (all.isEmpty()) {
            log.warn("등록된 사용자가 없습니다.");
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        return all.map(AlarmMapper::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<AlarmResponseDto> searchAlarmList(String userEmail, int page, int size, String sortBy, boolean isAsc) {
        // 페이징 처리를 하기 위한 Sort 객체 생성
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        // page, size, Sort객체로 Pageable 객체 완성
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Alarm> byUserEmail = alarmRepository.findByUserEmailContaining(userEmail, pageable);
        if (byUserEmail.isEmpty()) {
            log.warn("해당 조건으로 등록된 사용자가 없습니다.");
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        return byUserEmail.map(AlarmMapper::fromEntity);
    }

}
