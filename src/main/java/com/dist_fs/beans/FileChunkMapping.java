package com.dist_fs.beans;

import com.dist_fs.beans.model.Chunk;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FileChunkMapping {
    private final HashMap<String, Chunk[]> fileChunkMapping = new HashMap<>();

    public HashMap<String, Chunk[]> getFileChunkMapping() {
        return fileChunkMapping;
    }
}
