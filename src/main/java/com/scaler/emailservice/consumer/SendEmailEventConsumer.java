package com.scaler.emailservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaler.emailservice.dtos.SendEmailDto;
import com.scaler.emailservice.utils.EmailUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

@Service
public class SendEmailEventConsumer {

    private ObjectMapper objectMapper;

    public SendEmailEventConsumer() {
        objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "sendEmail1", groupId = "emailService1")
    public void handSendEmailEvent(String message) throws JsonProcessingException {
        SendEmailDto sendEmailDto = objectMapper.readValue(message, SendEmailDto.class);

        final String fromEmail = sendEmailDto.getFrom(); //requires valid gmail id
        final String toEmail = sendEmailDto.getTo(); // can be any email id
        final String subject = sendEmailDto.getSubject();
        final String body = sendEmailDto.getBody();

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, "");
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, toEmail,subject, body);
    }

    // Break for 7 minutes: 10:27 -> 10:35
}