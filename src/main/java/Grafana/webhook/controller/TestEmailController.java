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

            byte[] imageBytes = grafanaImageFetcher.fetchImage("https://mevin.online/grafana/render/d-solo/cetw573guhb0gf/new-dashboard?orgId=1&panelId=1&width=1000&height=500"
+ //
                                "");

            // emailSender.sendEmailWithImage(
            //         "stanleyonyango84@gmail.com",  // Replace with your test recipient
            //         "Grafana Alert: Service Down",
            //         html,
            //         imageBytes
            // );

            return "✅ Email sent successfully!";
        } catch (MessagingException e) {
            return "❌ Email failed: " + e.getMessage();
        } catch (Exception e) {
            return "❌ Error fetching image: " + e.getMessage();
        }
    }
    
}
