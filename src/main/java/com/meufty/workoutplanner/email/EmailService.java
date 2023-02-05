package com.meufty.workoutplanner.email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeMessage;

import javax.mail.MessagingException;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email address");
            helper.setFrom("do-not-reply@workoutplanner.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e){
            logger.error("Failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
