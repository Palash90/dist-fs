package com.dist_fs;

import com.dist_fs.beans.ChunkServerDetails;
import com.dist_fs.beans.ChunkToServerMapping;
import com.dist_fs.beans.FileChunkMapping;
import com.dist_fs.beans.model.Chunk;
import com.dist_fs.beans.model.UploadDownloadResponse;
import com.dist_fs.services.DownloadService;
import com.dist_fs.services.StatusCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class DownloadServiceTest {
    @InjectMocks
    private DownloadService downloadService;

    @Mock
    private StatusCheckService statusService;

    @Mock
    private FileChunkMapping fileChunkMapping;

    @Mock
    private ChunkToServerMapping chunkToServerMapping;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    public void testDownload() {
        UUID[] uuids = new UUID[]{UUID.randomUUID(), UUID.randomUUID()};

        HashMap<String, Chunk[]> mockFileChunkMapping = new HashMap<>();

        Chunk chunk1 = new Chunk();
        chunk1.setChunkId(uuids[0]);
        chunk1.setChunkSize(1024);

        Chunk chunk2 = new Chunk();
        chunk2.setChunkSize(512);
        chunk2.setChunkId(uuids[1]);

        Chunk[] chunks = new Chunk[]{chunk1, chunk2};
        mockFileChunkMapping.put("a.txt", chunks);

        HashMap<UUID, ChunkServerDetails[]> chunkServerDetailsHashMap = getUuidHashMap(uuids);


        when(statusService.getChunkStatus()).thenReturn(getMockChunkServerDetails());
        when(chunkToServerMapping.getChunkToServerMapping()).thenReturn(chunkServerDetailsHashMap);
        when(fileChunkMapping.getFileChunkMapping()).thenReturn(mockFileChunkMapping);


        UploadDownloadResponse[] response = downloadService.getDownloadResponse("a.txt");
        assertEquals(2, response.length);

        assertEquals(uuids[0], response[0].getChunk().getChunkId());
        assertEquals(1024, response[0].getChunk().getChunkSize());
        assertEquals(3, response[0].getUrls().size());
        assertEquals("chunk1/download", response[0].getUrls().get(0));
        assertEquals("chunk2/download", response[0].getUrls().get(1));
        assertEquals("chunk3/download", response[0].getUrls().get(2));

        assertEquals(uuids[1], response[1].getChunk().getChunkId());
        assertEquals(512, response[1].getChunk().getChunkSize());
        assertEquals(3, response[1].getUrls().size());
        assertEquals("chunk4/download", response[1].getUrls().get(0));
        assertEquals("chunk1/download", response[1].getUrls().get(1));
        assertEquals("chunk2/download", response[1].getUrls().get(2));
    }

    @Test
    public void testDownloadOneChunkDown() {
        UUID[] uuids = new UUID[]{UUID.randomUUID(), UUID.randomUUID()};

        HashMap<String, Chunk[]> mockFileChunkMapping = new HashMap<>();

        Chunk chunk1 = new Chunk();
        chunk1.setChunkId(uuids[0]);
        chunk1.setChunkSize(1024);

        Chunk chunk2 = new Chunk();
        chunk2.setChunkSize(512);
        chunk2.setChunkId(uuids[1]);

        Chunk[] chunks = new Chunk[]{chunk1, chunk2};
        mockFileChunkMapping.put("a.txt", chunks);

        HashMap<UUID, ChunkServerDetails[]> chunkServerDetailsHashMap = getUuidHashMap(uuids);


        when(statusService.getChunkStatus()).thenReturn(getMockChunkServerDetailsOneChunkDown());
        when(chunkToServerMapping.getChunkToServerMapping()).thenReturn(chunkServerDetailsHashMap);
        when(fileChunkMapping.getFileChunkMapping()).thenReturn(mockFileChunkMapping);


        UploadDownloadResponse[] response = downloadService.getDownloadResponse("a.txt");
        assertEquals(2, response.length);

        assertEquals(uuids[0], response[0].getChunk().getChunkId());
        assertEquals(1024, response[0].getChunk().getChunkSize());
        assertEquals(2, response[0].getUrls().size());
        assertEquals("chunk2/download", response[0].getUrls().get(0));
        assertEquals("chunk3/download", response[0].getUrls().get(1));

        assertEquals(uuids[1], response[1].getChunk().getChunkId());
        assertEquals(512, response[1].getChunk().getChunkSize());
        assertEquals(2, response[1].getUrls().size());
        assertEquals("chunk4/download", response[1].getUrls().get(0));
        assertEquals("chunk2/download", response[1].getUrls().get(1));
    }

    private static HashMap<UUID, ChunkServerDetails[]> getUuidHashMap(UUID[] uuids) {
        HashMap<UUID, ChunkServerDetails[]> chunkServerDetailsHashMap = new HashMap<>();
        ChunkServerDetails[] chunkServerDetails1 = getChunkServerDetails1();
        chunkServerDetailsHashMap.put(uuids[0], chunkServerDetails1);


        ChunkServerDetails[] chunkServerDetails2 = getChunkServerDetails2();
        chunkServerDetailsHashMap.put(uuids[1], chunkServerDetails2);
        return chunkServerDetailsHashMap;
    }

    private static ChunkServerDetails[] getChunkServerDetails2() {
        ChunkServerDetails chunkServerDetails1 = new ChunkServerDetails();
        chunkServerDetails1.setUrl("chunk4");

        ChunkServerDetails chunkServerDetails2 = new ChunkServerDetails();
        chunkServerDetails2.setUrl("chunk1");

        ChunkServerDetails chunkServerDetails3 = new ChunkServerDetails();
        chunkServerDetails3.setUrl("chunk2");

        return new ChunkServerDetails[]{chunkServerDetails1, chunkServerDetails2, chunkServerDetails3};
    }

    private static ChunkServerDetails[] getChunkServerDetails1() {
        ChunkServerDetails chunkServerDetails1 = new ChunkServerDetails();
        chunkServerDetails1.setUrl("chunk1");

        ChunkServerDetails chunkServerDetails2 = new ChunkServerDetails();
        chunkServerDetails2.setUrl("chunk2");

        ChunkServerDetails chunkServerDetails3 = new ChunkServerDetails();
        chunkServerDetails3.setUrl("chunk3");

        return new ChunkServerDetails[]{chunkServerDetails1, chunkServerDetails2, chunkServerDetails3};
    }

    private static LinkedHashMap<String, ChunkServerDetails> getMockChunkServerDetails() {
        LinkedHashMap<String, ChunkServerDetails> mockChunkServerDetails = new LinkedHashMap<>();
        ChunkServerDetails chunk1 = new ChunkServerDetails();
        chunk1.setLive(true);
        chunk1.setUrl("chunk1");
        ChunkServerDetails chunk2 = new ChunkServerDetails();
        chunk2.setLive(true);
        chunk2.setUrl("chunk2");
        ChunkServerDetails chunk3 = new ChunkServerDetails();
        chunk3.setLive(true);
        chunk3.setUrl("chunk3");
        ChunkServerDetails chunk4 = new ChunkServerDetails();
        chunk4.setLive(true);
        chunk4.setUrl("chunk4");

        mockChunkServerDetails.put("chunk-1", chunk1);
        mockChunkServerDetails.put("chunk-2", chunk2);
        mockChunkServerDetails.put("chunk-3", chunk3);
        mockChunkServerDetails.put("chunk-4", chunk4);
        return mockChunkServerDetails;
    }

    private static LinkedHashMap<String, ChunkServerDetails> getMockChunkServerDetailsOneChunkDown() {
        LinkedHashMap<String, ChunkServerDetails> mockChunkServerDetails = new LinkedHashMap<>();
        ChunkServerDetails chunk1 = new ChunkServerDetails();
        chunk1.setLive(false);
        chunk1.setUrl("chunk1");
        ChunkServerDetails chunk2 = new ChunkServerDetails();
        chunk2.setLive(true);
        chunk2.setUrl("chunk2");
        ChunkServerDetails chunk3 = new ChunkServerDetails();
        chunk3.setLive(true);
        chunk3.setUrl("chunk3");
        ChunkServerDetails chunk4 = new ChunkServerDetails();
        chunk4.setLive(true);
        chunk4.setUrl("chunk4");

        mockChunkServerDetails.put("chunk-1", chunk1);
        mockChunkServerDetails.put("chunk-2", chunk2);
        mockChunkServerDetails.put("chunk-3", chunk3);
        mockChunkServerDetails.put("chunk-4", chunk4);
        return mockChunkServerDetails;
    }
}
