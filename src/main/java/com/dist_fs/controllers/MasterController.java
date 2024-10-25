package com.dist_fs.controllers;

import com.dist_fs.beans.UploadRequest;
import com.dist_fs.beans.UploadResponse;
import com.dist_fs.services.StatusCheckService;
import com.dist_fs.services.UploadService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@ConditionalOnProperty(name = "mode", havingValue = "master")
@EnableScheduling
public class MasterController {
    @Autowired
    private Environment environment;

    @Autowired
    private StatusCheckService statusCheckService;

    @Autowired
    private UploadService uploadService;

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

    @PostMapping("upload")
    public List<UploadResponse> masterApi(@RequestBody UploadRequest request) {
        System.out.println(request);
        return uploadService.getUploadResponse(request);
    }
}
