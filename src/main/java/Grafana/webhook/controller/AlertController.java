package Grafana.webhook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import Grafana.webhook.service.EmailAlertService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    
    @Autowired
    private EmailAlertService emailAlertService;

    @PostMapping("path")
    public ResponseEntity<String> receiveAlert(@RequestBody JsonNode alertJson) {
        
        try {
            emailAlertService.processAlert(alertJson);
            return ResponseEntity.ok("Alert received and email sent");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to process alert: " + e.getMessage());
        }
    }
}
    

