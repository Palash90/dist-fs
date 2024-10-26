package com.dist_fs.beans;

import java.util.List;

public class UploadResponse {
    private List<String> urls;

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    private Chunk chunk;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UUID: ").append(this.chunk.getChunkId());
        sb.append("Chunk Size: ").append(this.chunk.getChunkSize());
        sb.append("Upload to:\n");
        for (String u : urls) {
            sb.append("\t").append(u);
        }
        return sb.toString();
    }
}
