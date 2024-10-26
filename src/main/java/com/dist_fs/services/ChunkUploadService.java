package com.dist_fs.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class ChunkUploadService {
    @Value("${uploadDirectory}")
    private String UPLOAD_DIR;

    public void saveByteStreamToDisk(UUID chunkId, byte[] byteStream) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(UPLOAD_DIR + "/" + chunkId.toString());
        fos.write(byteStream);
        fos.close();
    }
}
