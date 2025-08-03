package Grafana.webhook.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import Grafana.webhook.service.EmailSender;
import Grafana.webhook.util.GrafanaImageFetcher;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestEmailController {

    private final EmailSender emailSender;
    private final GrafanaImageFetcher grafanaImageFetcher;

    @GetMapping("/send-test-email")
    public String sendTestEmail() {
        try {
            String html = """
                <html>
                <body>
                    <h2>🚨 Grafana Alert Received</h2>
                    <p>See below the affected panel:</p>
                    <img src="cid:grafana-image" alt="Grafana Panel"/>
                </body>
                </html>
                """;

            byte[] imageBytes = grafanaImageFetcher.fetchImage("/render/d-solo/8fd2bd26-02c9-4766-af1a-1f8147e7c0ca/new-dashboard?orgId=1&panelId=1&width=1000&height=500&tz=UTC\n" + //
                                "");

            emailSender.sendEmailWithImage(
                    "stanleyonyango84@gmail.com",  // Replace with your test recipient
                    "Grafana Alert: Service Down",
                    html,
                    imageBytes
            );

            return "✅ Email sent successfully!";
        } catch (MessagingException e) {
            return "❌ Email failed: " + e.getMessage();
        } catch (Exception e) {
            return "❌ Error fetching image: " + e.getMessage();
        }
    }
}
