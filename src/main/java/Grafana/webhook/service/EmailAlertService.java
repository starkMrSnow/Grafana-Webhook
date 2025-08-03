package Grafana.webhook.service;

import Grafana.webhook.util.GrafanaImageFetcher;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmailAlertService {

    @Autowired
    private GrafanaImageFetcher imageFetcher;

    @Autowired
    private EmailSender emailSender;

    public void processAlert(JsonNode alertJson) {
        List<String> htmlBlocks = new ArrayList<>();
        List<EmailSender.EmbeddedImage> imageAttachments = new ArrayList<>();

        int count = 1;

        for (JsonNode alert : alertJson.path("alerts")) {
            String alertName = alert.path("labels").path("alertname").asText();
            String severity = alert.path("labels").path("severity").asText();
            String summary = alert.path("annotations").path("summary").asText();
            String panelPath = alert.path("labels").path("panelPath").asText();

            log.info("Processing alert: {} (panelPath: {})", alertName, panelPath);

            String contentId = "image-" + count;

            try {
                byte[] imageBytes = imageFetcher.fetchImage(panelPath);

                // Attach image for inline embedding
                imageAttachments.add(new EmailSender.EmbeddedImage(contentId, imageBytes));

                htmlBlocks.add(String.format(
                        """
                        <div style="margin-bottom:20px;">
                          <h3>%s [%s]</h3>
                          <p>%s</p>
                          <img src="cid:%s" style="max-width:100%%; border:1px solid #ccc;" />
                        </div>
                        """, alertName, severity, summary, contentId
                ));
            } catch (Exception e) {
                log.error("❌ Error fetching image for alert '{}': {}", alertName, e.getMessage());

                htmlBlocks.add(String.format(
                        """
                        <div style="margin-bottom:20px;">
                          <h3>%s [%s]</h3>
                          <p>%s</p>
                          <p style="color:red;">⚠️ Failed to load alert image.</p>
                        </div>
                        """, alertName, severity, summary
                ));
            }

            count++;
        }

        String finalHtmlBody = """
            <html>
              <body>
                <h2>🚨 Sidian Alerts</h2>
                %s
              </body>
            </html>
            """.formatted(String.join("\n", htmlBlocks));

        try {
            emailSender.sendEmailWithAttachments(
                    "stanleyonyango84@gmail.com",
                    "🚨 Sidian Alert Notification",
                    finalHtmlBody,
                    imageAttachments
            );
            log.info("✅ Email sent successfully");

        } catch (MessagingException e) {
            log.error("❌ Failed to send email: {}", e.getMessage());
        }
    }
}
