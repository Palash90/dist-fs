package com.dist_fs.controllers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(name = "mode", havingValue = "chunk")
public class SlaveController {

    @GetMapping("/hb")
    public void hearbeat() {
        return;
    }

    @GetMapping("/chunk")
    public String slaveApi() {
        return "Chunk API response";
    }
}

