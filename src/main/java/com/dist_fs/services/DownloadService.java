package com.dist_fs.services;

import com.dist_fs.beans.ChunkServerDetails;
import com.dist_fs.beans.ChunkToServerMapping;
import com.dist_fs.beans.FileChunkMapping;
import com.dist_fs.beans.model.Chunk;
import com.dist_fs.beans.model.UploadDownloadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DownloadService {
    @Autowired
    private StatusCheckService stausService;

    @Autowired
    private FileChunkMapping fileChunkMapping;

    @Autowired
    private ChunkToServerMapping chunkToServerMapping;

    public UploadDownloadResponse[] getDownloadResponse(String filePath) {
        List<ChunkServerDetails> liveChunkServers = stausService
                .getChunkStatus()
                .values()
                .stream()
                .filter(ChunkServerDetails::isLive)
                .toList();

        Chunk[] chunks = fileChunkMapping.getFileChunkMapping().get(filePath);
        UploadDownloadResponse[] response = new UploadDownloadResponse[chunks.length];

        for (int i = 0; i < chunks.length; i++) {
            response[i] = new UploadDownloadResponse();
            response[i].setChunk(chunks[i]);

            ChunkServerDetails[] chunkServerDetails = chunkToServerMapping.getChunkToServerMapping().get(chunks[i].getChunkId());
            List<String> urls = Arrays.stream(chunkServerDetails)
                    .filter(server -> liveChunkServers.stream()
                            .anyMatch(liveServer -> liveServer.getUrl().equals(server.getUrl())))
                    .toList().stream().map(c -> c.getUrl() + "/download").toList();

            response[i].setUrls(urls);
        }

        return response;
    }
}
