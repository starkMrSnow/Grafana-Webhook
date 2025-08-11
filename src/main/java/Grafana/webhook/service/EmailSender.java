package Grafana.webhook.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender javaMailSender;

    public void sendEmailWithAttachments(String to, String subject, String htmlBody, List<EmbeddedImage> images) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("sidian@gmail.com");
        helper.setText(htmlBody, true);

        for (EmbeddedImage img : images) {
            helper.addInline(img.contentId(), new ByteArrayResource(img.bytes()), "image/png");
   
        }

        javaMailSender.send(message);
    }

    public record EmbeddedImage(String contentId, byte[] bytes) {}
}
