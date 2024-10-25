package com.dist_fs.services;

import com.dist_fs.beans.ChunkServerDetails;
import com.dist_fs.beans.ServerDetails;
import com.dist_fs.beans.UploadRequest;
import com.dist_fs.beans.UploadResponse;
import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UploadService {
    @Autowired
    private StatusCheckService stausService;

    @Autowired
    private ServerDetails severDetails;

    public List<UploadResponse> getUploadResponse(UploadRequest request) {
        List<UploadResponse> chunks = new ArrayList<>();
        int numChunks = (int) (request.getFileSize() / severDetails.getChunkSize()) + 1;
        long remainingSize = request.getFileSize();

        stausService
                .getChunkStatus()
                .values()
                .stream()
                .filter(ChunkServerDetails::isLive)
                .toList();


        return chunks;
    }

}
