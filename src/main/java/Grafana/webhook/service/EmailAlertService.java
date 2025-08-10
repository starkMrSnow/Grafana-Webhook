package Grafana.webhook.service;

import Grafana.webhook.util.GrafanaImageFetcher;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class EmailAlertService {

    @Autowired
    private GrafanaImageFetcher imageFetcher;

    @Autowired
    private EmailSender emailSender;

    @Autowired 
    private TemplateEngine templateEngine;

   
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

        // Create a Thymeleaf context and add the list of alerts
        Context context = new Context();
        context.setVariable("alerts", alertDataList);

        // Process the template to generate the final HTML string
        String finalHtmlBody = templateEngine.process("alert_template", context);

        try {
            emailSender.sendEmailWithAttachments(
                    "stanley.otieno@giktek.io ",
                    "🚨 Sidian Alert Notification",
                    finalHtmlBody,
                    imageAttachments
            );
            log.info("Email sent successfully");

        } catch (MessagingException e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }
}