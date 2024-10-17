package com.sparta.sal.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendSlackInviteEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("SAL의 Slack 워크스페이스로 초대합니다!!");
        message.setTo(email);
        message.setText("아래의 링크를 이용하여 저희의 워크스페이스로 오시면 각종 알림을 받으실 수 있습니다.\nhttps://join.slack.com/t/sal-tbh6405/shared_invite/zt-2shyrhyiq-N2TDOOfrqxL8RQA5Lp9ARw");

        javaMailSender.send(message);
    }

    public void sendFindPasswordToken(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("SAL의 비밀번호 재설정을 위한 인증번호입니다.");
        message.setTo(email);
        message.setText("아래의 인증번호를 이용하여 비밀번호를 재설정하실 수 있습니다.\n인증번호는 30분 동안만 유효합니다.\n" + token);

        javaMailSender.send(message);
    }
}
