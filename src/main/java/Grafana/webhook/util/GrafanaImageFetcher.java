package Grafana.webhook.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GrafanaImageFetcher {
     @Value("${grafana.base-url}")
    private String grafanaBaseUrl;

    @Value("${grafana.api-key}")
    private String grafanaApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public byte[] fetchImage(String panelRenderPath) throws Exception {
        String fullUrl = panelRenderPath.startsWith("http")
                ? panelRenderPath
                : grafanaBaseUrl + panelRenderPath;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + grafanaApiKey);
        headers.setAccept(MediaType.parseMediaTypes("image/png"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                fullUrl,
                HttpMethod.GET,
                entity,
                byte[].class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch image: " + response.getStatusCode());
        }
    }

}
