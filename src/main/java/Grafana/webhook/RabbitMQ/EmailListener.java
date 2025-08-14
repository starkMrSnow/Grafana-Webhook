package Grafana.webhook.RabbitMQ;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import Grafana.webhook.dto.EmailMessageDto;
import Grafana.webhook.service.EmailSender;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailListener {

    @Autowired  
    private EmailSender emailSender;

    // private EmailMessageDto message;

    @RabbitListener(queues = "emailQueue")
    public void handleEmailMessage(EmailMessageDto message){

        log.info("received message successfully");

        try {
          emailSender.sendEmailWithAttachments(
           message.getTo(),
           message.getSubject(),
           message.getBody(),
           message.getImages());
            log.info("email sent successfully after receiving from queue");
        } catch (MessagingException e) {
            log.error("failed to send email after receiving from queue: {}", e.getMessage());
        }
    }
}