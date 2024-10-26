package com.dist_fs.controllers;

import com.dist_fs.services.ChunkUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@ConditionalOnProperty(name = "mode", havingValue = "chunk")
public class ChunkServerController {
    @Autowired
    private ChunkUploadService chunkUploadService;

    @GetMapping("/hb")
    public ResponseEntity<String> hearbeat() {
        return new ResponseEntity<>("Alive", HttpStatus.OK);
    }

    @PostMapping("/upload")
    public @ResponseBody ResponseEntity<String> upload(@RequestHeader("chunkId") UUID chunkId, @RequestBody byte[] byteStream) {
        try {
            chunkUploadService.saveByteStreamToDisk(chunkId, byteStream);
            return new ResponseEntity<>("Chunk uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

