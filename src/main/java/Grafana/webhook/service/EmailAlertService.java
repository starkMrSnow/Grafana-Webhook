package Grafana.webhook.service;

import Grafana.webhook.RabbitMQ.RabbitMQConfig;
import Grafana.webhook.util.GrafanaImageFetcher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Attr;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class EmailAlertService {
   
    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private GrafanaImageFetcher imageFetcher;

    @Autowired
    private EmailSender emailSender;

    @Autowired 
    private TemplateEngine templateEngine;

    @Autowired
    private RabbitTemplate rabbitTemplate;

   
    public record AlertData(String alertName, String severity, String summary, String contentId) {}

    public void processAlert(JsonNode alertJson) {
        // List to hold the alert data objects
        List<AlertData> alertDataList = new ArrayList<>();
        List<EmailSender.EmbeddedImage> imageAttachments = new ArrayList<>();

        int count = 1;

        for (JsonNode alert : alertJson.path("alerts")) {
            String alertName = alert.path("labels").path("alertname").asText("Unknown Alert");        
            String severity = alert.path("labels").path("severity").asText("N/A");
            String summary = alert.path("annotations").path("summary").asText();
            String panelPath = alert.path("labels").path("panelPath").asText();
            String contentId = "image-" + count;

            log.info("Processing alert: {} (panelPath: {})", alertName, panelPath);

            try {
                byte[] imageBytes = imageFetcher.fetchImage(panelPath);
                imageAttachments.add(new EmailSender.EmbeddedImage(contentId, imageBytes));
                alertDataList.add(new AlertData(alertName, severity, summary, contentId));


            } catch (Exception e) {
                log.error("Error fetching image for alert '{}': {}", alertName, e.getMessage());
                // If the image fails, pass 'null' for the contentId to show the error message in the template
                alertDataList.add(new AlertData(alertName, severity, summary, null));
            }

            count++;
        }

       
        Context context = new Context();
        context.setVariable("alerts", alertDataList);

    
        String finalHtmlBody = templateEngine.process("alert_template", context);

        try {
           
            Map<String, Object> emailMessage = new HashMap<>();
            emailMessage.put( "to", "stanley.otieno@giktek.io");
            emailMessage.put("subject","🚨 Sidian Alert Notification");
            emailMessage.put("body", finalHtmlBody);
            emailMessage.put("images", imageAttachments);

            rabbitTemplate.convertAndSend("emailExchange", "emailQueue", emailMessage);

            log.info("Email sent to queue successfully");

        } catch (Exception e) {
            log.error("Failed to send email to the queue");
        }
    }
}