package com.dist_fs.beans;

import java.util.List;
import java.util.UUID;

public class UploadResponse {
    private List<String> urls;
    private UUID chunkId;
    private long chunkSize;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UUID: ").append(this.chunkId);
        sb.append("Chunk Size: ").append(this.chunkSize);
        sb.append("Upload to:\n");
        for (String u : urls) {
            sb.append("\t").append(u);
        }
        return sb.toString();
    }
}
