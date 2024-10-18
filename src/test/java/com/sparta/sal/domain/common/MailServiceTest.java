package com.sparta.sal.domain.common;

import com.sparta.sal.common.service.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void sendSlackInviteEmail_Success() {
        // Given
        String email = "test@example.com";

        // When
        mailService.sendSlackInviteEmail(email);

        // Then
        verify(javaMailSender).send(any(SimpleMailMessage.class)); // SimpleMailMessage가 정확히 설정되었는지는 추가적인 검증을 통해 확인 가능
    }

    @Test
    void sendFindPasswordToken_Success() {
        // Given
        String email = "test@example.com";
        String token = "123456";

        // When
        mailService.sendFindPasswordToken(email, token);

        // Then
        verify(javaMailSender).send(any(SimpleMailMessage.class)); // SimpleMailMessage가 정확히 설정되었는지는 추가적인 검증을 통해 확인 가능
    }
}
