package com.dist_fs.controllers;

import com.dist_fs.services.StatusCheckService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(name = "mode", havingValue = "master")
@EnableScheduling
public class MasterController {
    @Autowired
    private Environment environment;

    @Autowired
    private StatusCheckService statusCheckService;

    @PostConstruct
    public void init() {
        String envVar = environment.getProperty("DIST_FS_CHUNKS");
        statusCheckService.initiateStatusCheck(envVar);
    }

    @GetMapping("/hb")
    public ResponseEntity<String> heartbeat() {
        StringBuilder hbMap = new StringBuilder("Chunk Server Status: \n");
        statusCheckService.getChunkStatus().forEach((k, v) -> hbMap.append("\t" + k + " : " + v + "\n"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain; charset=utf-8");

        return new ResponseEntity<>(hbMap.toString(), headers, HttpStatus.OK);
    }

    @GetMapping("/master")
    public String masterApi() {
        return "Master API response";
    }
}
