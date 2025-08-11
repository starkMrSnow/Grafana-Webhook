package Grafana.webhook.RabbitMQ;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import Grafana.webhook.service.EmailSender;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailListener {

    @Autowired  
    private EmailSender emailSender;

    @RabbitListener(queues = "emailQueue")
    public void handleEmailMessage(Map<String, Object> message){

        log.info("received message successfully");

        try {
            String to = (String) message.get("to");
            String subject = (String) message.get("subject");
            String body = (String) message.get("body");
            List<EmailSender.EmbeddedImage> images = (List<EmailSender.EmbeddedImage>) message.get("images");

            emailSender.sendEmailWithAttachments(to, subject, body, images);
            log.info("email sent successfully afyer receiving from queue");
        } catch (MessagingException e) {
            log.error("failed to send email after receiving from queue: {}", e.getMessage());
        }
    }
}