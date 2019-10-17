package com.mcoder.kft;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public interface MailHandler {

    default void sendSimpleMail(JavaMailSender mailSender, String fromEmail, String toEmail, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);
        mailSender.send(message);
    }
}
