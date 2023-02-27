package com.meufty.workoutplanner.email;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailListener {
    @Value("${spring.kafka.template.email-topic}")
    private String emailTopic;

    private final JavaMailSender mailSender;

    @Autowired
    public EmailListener(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "${spring.kafka.template.email-topic}")
    public void receive(EmailMessage message) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(message.getTo());
        helper.setSubject("Confirm your email address");
        helper.setFrom("do-not-reply@workoutplanner.com");
        helper.setText(message.getBody(), true);
        mailSender.send(mimeMessage);
    }
}
