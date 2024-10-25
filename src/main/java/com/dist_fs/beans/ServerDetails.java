package com.dist_fs.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerDetails {
    @Value("${chunkSize}")
    private int chunkSize;

    @Value("${minReplica}")
    private int minReplica;

    public int getChunkSize() {
        return chunkSize;
    }

    public int getMinReplica() {
        return minReplica;
    }
}
