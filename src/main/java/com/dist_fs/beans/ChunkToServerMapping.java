package com.dist_fs.beans;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Component
public class ChunkToServerMapping {
    public HashMap<UUID, ChunkServerDetails[]> getChunkToServerMapping() {
        return chunkToServerMapping;
    }

    private final HashMap<UUID, ChunkServerDetails[]> chunkToServerMapping = new HashMap<>();
}
