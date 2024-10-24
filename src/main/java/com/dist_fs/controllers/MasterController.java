package com.dist_fs.controllers;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@RestController
@ConditionalOnProperty(name = "mode", havingValue = "master")
@EnableScheduling
public class MasterController {
    @Autowired
    private Environment environment;
    private final HashMap<String, Boolean> chunkStatus = new HashMap<String, Boolean>();
    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        String envVar = environment.getProperty("CHUNKS");
        String[] chunks = envVar != null ? envVar.split(";") : new String[]{};

        for (String chunk : chunks) {
            chunkStatus.put(chunk, false);
        }
    }

    @Scheduled(fixedRate = 1000)
    public void checkClientStatus() {
        for (String chunk : chunkStatus.keySet()) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(chunk, String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    chunkStatus.put(chunk, true);
                } else {
                    chunkStatus.put(chunk, false);
                }
            } catch (Exception e) {
                chunkStatus.put(chunk, false);
            }
        }
    }

    @GetMapping("/hb")
    public ResponseEntity<String> heartbeat() {
        StringBuilder hbMap = new StringBuilder("Chunk Server Status: \n");
        this.chunkStatus.forEach((k, v) -> hbMap.append("\t" + k + " : " + v + "\n"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain; charset=utf-8");

        return new ResponseEntity<>(hbMap.toString(), headers, HttpStatus.OK);
    }

    @GetMapping("/master")
    public String masterApi() {
        return "Master API response";
    }
}
