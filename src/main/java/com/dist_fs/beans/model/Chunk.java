package com.dist_fs.beans.model;

import java.util.UUID;

public class Chunk {
    private UUID chunkId;
    private long chunkSize;

    public UUID getChunkId() {
        return chunkId;
    }

    public void setChunkId(UUID chunkId) {
        this.chunkId = chunkId;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }
}
