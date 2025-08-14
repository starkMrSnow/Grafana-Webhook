package Grafana.webhook.dto;

import java.util.List;

import Grafana.webhook.service.EmailSender.EmbeddedImage;


public class EmailMessageDto {
    private String to;
    private String subject;
    private String body;
    private List<EmbeddedImage> images;

    
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<EmbeddedImage> getImages() {
        return images;
    }

    public void setImages(List<EmbeddedImage> images) {
        this.images = images;
    }
}
