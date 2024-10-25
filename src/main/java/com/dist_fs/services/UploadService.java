package com.dist_fs.services;

import com.dist_fs.beans.ChunkServerDetails;
import com.dist_fs.beans.ServerDetails;
import com.dist_fs.beans.UploadRequest;
import com.dist_fs.beans.UploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {
    @Autowired
    private StatusCheckService stausService;

    @Autowired
    private ServerDetails serverDetails;

    private int roundRobinPointer = 0;

    public List<UploadResponse> getChunks(UploadRequest request) {
        List<UploadResponse> chunks = new ArrayList<>();

        List<ChunkServerDetails> liveChunkServers = stausService
                .getChunkStatus()
                .values()
                .stream()
                .filter(ChunkServerDetails::isLive)
                .toList();

        long remainingSize = request.getFileSize();

        while(remainingSize > serverDetails.getChunkSize()) {
            addNewChunk(liveChunkServers, serverDetails.getChunkSize(), chunks);
            remainingSize -= serverDetails.getChunkSize();
        }

        if(remainingSize > 0) {
            addNewChunk(liveChunkServers, (int) remainingSize, chunks);
        }
        return chunks;
    }

    private void addNewChunk(List<ChunkServerDetails> liveChunkServers, int chunkSize, List<UploadResponse> chunks) {
        UploadResponse response = new UploadResponse();

        response.setChunkSize(chunkSize);
        response.setChunkId(UUID.randomUUID());

        assignChunkServers(liveChunkServers, response);
        chunks.add(response);
    }

    private void assignChunkServers(List<ChunkServerDetails> liveChunkServers,  UploadResponse response) {
        // For simplicity, just going with simple round-robin fashion. Later we can make use of other sophisticated algorithm.

        List<String> urls = new ArrayList<>();
        while(urls.size() < serverDetails.getMinReplica()) {
            urls.add(liveChunkServers.get(roundRobinPointer).getUrl());
            roundRobinPointer += 1;
            if(roundRobinPointer == liveChunkServers.size()){
                roundRobinPointer = 0;
            }
        }
        response.setUrls(urls);
    }

}
