package com.dist_fs.services;

import com.dist_fs.beans.ChunkServerDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class StatusCheckService {
    @Autowired
    private RestTemplate restTemplate;

    private final LinkedHashMap<String, ChunkServerDetails> chunkStatus = new LinkedHashMap<String, ChunkServerDetails>();

    public LinkedHashMap<String, ChunkServerDetails> getChunkStatus() {
        return chunkStatus;
    }

    public void initiateStatusCheck(String urls) {
        String[] chunks = urls != null ? urls.split(";") : new String[]{};

        for (String chunk : chunks) {
            ChunkServerDetails chunkServerDetails = new ChunkServerDetails();
            chunkServerDetails.setLive(false);
            chunkServerDetails.setUrl(chunk);
            chunkStatus.put(chunk, chunkServerDetails);
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
                        chunkStatus.get(chunk).setLive(false);
                        System.err.println("Error checking chunk " + chunk + ": " + e.getMessage());
                        return null;
                    });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void checkChunkStatus(String chunk) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(chunk + "/hb", String.class);
            chunkStatus.get(chunk).setLive(response.getStatusCode() == HttpStatus.OK);
        } catch (Exception e) {
            chunkStatus.get(chunk).setLive(false);
        }
    }
}
