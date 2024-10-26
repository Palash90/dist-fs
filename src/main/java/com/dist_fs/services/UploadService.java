package com.dist_fs.services;

import com.dist_fs.beans.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {
    @Autowired
    private StatusCheckService stausService;

    @Autowired
    private ServerDetails serverDetails;

    @Autowired
    private FileChunkMapping fileChunkMapping;

    @Autowired
    private ChunkToServerMapping chunkToServerMapping;

    public UploadResponse[] getUploadResponse(UploadRequest request) {
        Chunk[] chunks = distributeFileIntoChunks(request);
        fileChunkMapping.getFileChunkMapping().put(request.getFilePath(), chunks);

        HashMap<UUID, ChunkServerDetails[]> requestToServerMapping = assignServersToChunk(chunks);
        chunkToServerMapping.getChunkToServerMapping().putAll(requestToServerMapping);

        return getUploadResponses(chunks, requestToServerMapping);
    }

    private static UploadResponse[] getUploadResponses(Chunk[] chunks, HashMap<UUID, ChunkServerDetails[]> requestToServerMapping) {
        UploadResponse[] response = new UploadResponse[chunks.length];
        for (int i = 0; i < chunks.length; i++) {
            response[i] = new UploadResponse();
            response[i].setChunk(chunks[i]);
            response[i].setUrls(Arrays.stream(requestToServerMapping.get(chunks[i].getChunkId())).map(c -> c.getUrl()).toList());
        }
        return response;
    }

    private Chunk[] distributeFileIntoChunks(UploadRequest request) {
        long fileSize = request.getFileSize();
        int chunkSize = serverDetails.getChunkSize();
        int numOfChunks = fileSize % chunkSize == 0 ? (int) (fileSize / chunkSize) : (int) (fileSize / chunkSize) + 1;

        Chunk[] chunks = new Chunk[numOfChunks];
        long remainingSize = request.getFileSize();

        for (int i = 0; i < numOfChunks; i++) {
            chunks[i] = createNewChunk(remainingSize > chunkSize ? chunkSize : (int) remainingSize);
            remainingSize -= serverDetails.getChunkSize();
        }

        return chunks;
    }

    private Chunk createNewChunk(int chunkSize) {
        Chunk chunk = new Chunk();
        chunk.setChunkSize(chunkSize);
        chunk.setChunkId(UUID.randomUUID());
        return chunk;
    }

    private HashMap<UUID, ChunkServerDetails[]> assignServersToChunk(Chunk[] chunks) {
        // For simplicity, going with simple round-robin fashion. Later we can make use of other sophisticated algorithm.

        List<ChunkServerDetails> liveChunkServers = stausService
                .getChunkStatus()
                .values()
                .stream()
                .filter(ChunkServerDetails::isLive)
                .toList();

        int roundRobinPointer = 0;
        int numOfReplicas = Math.min(liveChunkServers.size(), serverDetails.getMinReplica());
        HashMap<UUID, ChunkServerDetails[]> chunkToServerMapping = new HashMap<>();

        for (int i = 0; i < chunks.length; i++) {
            ChunkServerDetails[] chunkServerDetails = new ChunkServerDetails[numOfReplicas];
            for (int j = 0; j < numOfReplicas; j++) {
                chunkServerDetails[j] = liveChunkServers.get(roundRobinPointer);
                roundRobinPointer += 1;
                if (roundRobinPointer == liveChunkServers.size()) {
                    roundRobinPointer = 0;
                }
            }
            chunkToServerMapping.put(chunks[i].getChunkId(), chunkServerDetails);
        }
        return chunkToServerMapping;
    }

}
