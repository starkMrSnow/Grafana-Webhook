package Grafana.webhook.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender javaMailSender;

    public void sendEmailWithImage(String to, String subject, String htmlBody, byte[] imageBytes) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        // true = multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("175dollarsnow@gmail.com"); // Optional override

        // Embed the image inline using a CID (Content-ID)
        helper.setText(htmlBody, true);
        helper.addInline("grafana-image", new ByteArrayResource(imageBytes), "image/png");

        javaMailSender.send(message);
    }
    
}
