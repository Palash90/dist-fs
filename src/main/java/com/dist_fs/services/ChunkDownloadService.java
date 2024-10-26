package com.dist_fs.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystemException;

@Service
public class ChunkDownloadService {
    @Value("${uploadDirectory}")
    private String UPLOAD_DIR;

    public byte[] getFileBytes(String filePath) throws IOException {

        System.out.println("Finding file: " + UPLOAD_DIR + "/" + filePath);
        File file = new File(UPLOAD_DIR + "/" + filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        int bytesRead;

        bytesRead = fileInputStream.read(buffer);

        if (bytesRead == -1) {
            throw new FileSystemException(filePath);
        }

        fileInputStream.close();
       return buffer;
    }
}
