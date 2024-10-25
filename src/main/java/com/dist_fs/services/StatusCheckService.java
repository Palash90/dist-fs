package com.dist_fs.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class StatusCheckService {
    @Autowired
    private RestTemplate restTemplate;

    private final HashMap<String, Boolean> chunkStatus = new HashMap<String, Boolean>();

    public HashMap<String, Boolean> getChunkStatus() {
        return chunkStatus;
    }

    public void initiateStatusCheck(String urls) {
        String[] chunks = urls != null ? urls.split(";") : new String[]{};

        for (String chunk : chunks) {
            chunkStatus.put(chunk, false);
        }
    }

    @Scheduled(fixedRate = 1000)
    public void checkClientStatus() {
        List<String> chunks = new ArrayList<>(chunkStatus.keySet());
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String chunk : chunks) {
            CompletableFuture<Void> future = CompletableFuture.
                    runAsync(() -> checkChunkStatus(chunk)).
                    orTimeout(2, TimeUnit.SECONDS).exceptionally(e -> {
                        chunkStatus.put(chunk, false);
                        System.err.println("Error checking chunk " + chunk + ": " + e.getMessage());
                        return null;
                    });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        chunkStatus.forEach((key, value) -> System.out.println(key + ": " + value));
    }

    private void checkChunkStatus(String chunk) {
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
