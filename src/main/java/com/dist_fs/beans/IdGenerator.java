package com.dist_fs.beans;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGenerator {
    public UUID getId() {
        return UUID.randomUUID();
    }
}
