package com.learning.emailnotificationservice.Consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.emailnotificationservice.DTOs.SendEmailMessageDTO;
import com.learning.emailnotificationservice.Utils.EmailUtil;
import org.springframework.kafka.annotation.KafkaListener;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

public class SendEmailConsumer {

    private EmailUtil emailUtil;
    private ObjectMapper objectMapper;

    public SendEmailConsumer(EmailUtil emailUtil, ObjectMapper objectMapper) {
        this.emailUtil = emailUtil;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "sendSignUpEmail", groupId = "emailNotificationService")
    public void handleSendEmail(String message){
        SendEmailMessageDTO emailMessageDTO = null;
        try {
             emailMessageDTO = objectMapper.readValue(message, SendEmailMessageDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        properties.put("mail.smtp.port", "587"); //TLS Port
        properties.put("mail.smtp.auth", "true"); //Enable Authentication
        properties.put("mail.smtp.starttls.enable", "true"); //Enable Start TLS

        //create authenticator object to pass in the session.getInstance argument
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("yadraj1803@gmail.com", "");
            }
        };

        Session session = Session.getInstance(properties, authenticator);

        emailUtil.sendEmail(session, emailMessageDTO.getTo(), emailMessageDTO.getSubject(),
                emailMessageDTO.getBody());
    }
}
