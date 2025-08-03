package Grafana.webhook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import Grafana.webhook.util.GrafanaImageFetcher;

@Service
public class EmailAlertService {

    @Autowired
    private GrafanaImageFetcher imageFetcher;

    @Autowired
    private EmailSender emailSender;

    public void processAlert(JsonNode alertJson)  throws Exception {
        
        String alertName = alertJson.path("alerts").get(0).path("labels").path("alertname").asText();
        String planeUrl = "http://localhost:32007/render/d-solo/your-dashboard-uid/your-panel?panelId=2&width=1000&height=500&tz=UTC";

        //Fetch the image from Grafana
        byte[] imageBytes = imageFetcher.fetchImage(planeUrl);

        emailSender.sendEmailWithImage("admin@example.com", " Testing email", alertName, imageBytes); 
    }
    
}
