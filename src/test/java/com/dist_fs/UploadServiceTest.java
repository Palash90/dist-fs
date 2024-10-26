package com.dist_fs;

import com.dist_fs.beans.*;
import com.dist_fs.services.StatusCheckService;
import com.dist_fs.services.UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class UploadServiceTest {
    @InjectMocks
    private UploadService uploadService;

    @Mock
    private StatusCheckService statusService;

    @Mock
    private ServerDetails severDetails;

    @Mock
    private FileChunkMapping fileChunkMapping;

    @Mock
    private ChunkToServerMapping chunkToServerMapping;

    @Mock
    private IdGenerator idGenerator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    public void testFileChunking3Chunks() {
        LinkedHashMap<String, ChunkServerDetails> mockChunkServerDetails = getMockChunkServerDetails();
        HashMap<String, Chunk[]> mockFileChunkMapping = new HashMap<>();
        HashMap<UUID, ChunkServerDetails[]> chunkServerDetailsHashMap = new HashMap<>();
        UUID[] mockIds = new UUID[]{UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};

        when(statusService.getChunkStatus()).thenReturn(mockChunkServerDetails);
        when(severDetails.getChunkSize()).thenReturn(1024);
        when(severDetails.getMinReplica()).thenReturn(3);
        when(fileChunkMapping.getFileChunkMapping()).thenReturn(mockFileChunkMapping);
        when(chunkToServerMapping.getChunkToServerMapping()).thenReturn(chunkServerDetailsHashMap);
        when(idGenerator.getId()).thenReturn(mockIds[0]).thenReturn(mockIds[1]).thenReturn(mockIds[2]);

        UploadRequest request = new UploadRequest();
        request.setFilePath("a.txt");
        request.setFileSize(2093);
        UploadResponse[] uploadResponse = uploadService.getUploadResponse(request);

        assertEquals(3, uploadResponse.length);

        assertEquals(mockIds[0], uploadResponse[0].getChunk().getChunkId());
        assertEquals(1024, uploadResponse[0].getChunk().getChunkSize());
        List<String> firstChunkUrls = uploadResponse[0].getUrls();
        assertEquals(3, firstChunkUrls.size());
        assertEquals("chunk1", firstChunkUrls.get(0));
        assertEquals("chunk2", firstChunkUrls.get(1));
        assertEquals("chunk3", firstChunkUrls.get(2));

        assertEquals(mockIds[1], uploadResponse[1].getChunk().getChunkId());
        assertEquals(1024, uploadResponse[1].getChunk().getChunkSize());
        List<String> secondChunkUrls = uploadResponse[1].getUrls();
        assertEquals(3, secondChunkUrls.size());
        assertEquals("chunk4", secondChunkUrls.get(0));
        assertEquals("chunk1", secondChunkUrls.get(1));
        assertEquals("chunk2", secondChunkUrls.get(2));

        assertEquals(mockIds[2], uploadResponse[2].getChunk().getChunkId());
        assertEquals(45, uploadResponse[2].getChunk().getChunkSize());
        List<String> thirdChunkUrls = uploadResponse[2].getUrls();
        assertEquals(3, thirdChunkUrls.size());
        assertEquals("chunk3", thirdChunkUrls.get(0));
        assertEquals("chunk4", thirdChunkUrls.get(1));
        assertEquals("chunk1", thirdChunkUrls.get(2));

        assertEquals(1, mockFileChunkMapping.size());
        Chunk[] chunks = mockFileChunkMapping.get("a.txt");
        assertEquals(3, chunks.length);
        assertEquals(mockIds[0], chunks[0].getChunkId());
        assertEquals(1024, chunks[0].getChunkSize());
        assertEquals(mockIds[1], chunks[1].getChunkId());
        assertEquals(1024, chunks[1].getChunkSize());
        assertEquals(mockIds[2], chunks[2].getChunkId());
        assertEquals(45, chunks[2].getChunkSize());

        assertEquals(3, chunkServerDetailsHashMap.size());
        assertTrue(chunkServerDetailsHashMap.containsKey(mockIds[0]));
        ChunkServerDetails[] chunkServerDetails = chunkServerDetailsHashMap.get(mockIds[0]);
        assertEquals(3, chunkServerDetails.length);
        assertEquals(chunkServerDetails[0].getUrl(), "chunk1");
        assertEquals(chunkServerDetails[1].getUrl(), "chunk2");
        assertEquals(chunkServerDetails[2].getUrl(), "chunk3");

        chunkServerDetails = chunkServerDetailsHashMap.get(mockIds[1]);
        assertEquals(3, chunkServerDetails.length);
        assertEquals(chunkServerDetails[0].getUrl(), "chunk4");
        assertEquals(chunkServerDetails[1].getUrl(), "chunk1");
        assertEquals(chunkServerDetails[2].getUrl(), "chunk2");

        chunkServerDetails = chunkServerDetailsHashMap.get(mockIds[2]);
        assertEquals(3, chunkServerDetails.length);
        assertEquals(chunkServerDetails[0].getUrl(), "chunk3");
        assertEquals(chunkServerDetails[1].getUrl(), "chunk4");
        assertEquals(chunkServerDetails[2].getUrl(), "chunk1");
    }

    @Test
    public void testFileChunking1Chunk() {
        LinkedHashMap<String, ChunkServerDetails> mockChunkServerDetails = getMockChunkServerDetails();

        when(statusService.getChunkStatus()).thenReturn(mockChunkServerDetails);
        when(severDetails.getChunkSize()).thenReturn(1024);
        when(severDetails.getMinReplica()).thenReturn(3);

        UploadRequest request = new UploadRequest();
        request.setFilePath("a.txt");
        request.setFileSize(1024);
        UploadResponse[] uploadResponse = uploadService.getUploadResponse(request);

        assertEquals(1, uploadResponse.length);

        assertEquals(1024, uploadResponse[0].getChunk().getChunkSize());
        List<String> firstChunkUrls = uploadResponse[0].getUrls();
        assertEquals(3, firstChunkUrls.size());
        assertEquals("chunk1", firstChunkUrls.get(0));
        assertEquals("chunk2", firstChunkUrls.get(1));
        assertEquals("chunk3", firstChunkUrls.get(2));

    }

    @Test
    public void testFileChunking1ChunkSmallerThanConfig() {
        LinkedHashMap<String, ChunkServerDetails> mockChunkServerDetails = getMockChunkServerDetails();

        when(statusService.getChunkStatus()).thenReturn(mockChunkServerDetails);
        when(severDetails.getChunkSize()).thenReturn(1024);
        when(severDetails.getMinReplica()).thenReturn(3);

        UploadRequest request = new UploadRequest();
        request.setFilePath("a.txt");
        request.setFileSize(123);
        UploadResponse[] uploadResponse = uploadService.getUploadResponse(request);

        assertEquals(1, uploadResponse.length);

        assertEquals(123, uploadResponse[0].getChunk().getChunkSize());
        List<String> firstChunkUrls = uploadResponse[0].getUrls();
        assertEquals(3, firstChunkUrls.size());
        assertEquals("chunk1", firstChunkUrls.get(0));
        assertEquals("chunk2", firstChunkUrls.get(1));
        assertEquals("chunk3", firstChunkUrls.get(2));

    }

    @Test
    public void testFileChunkingExact4Chunks() {
        LinkedHashMap<String, ChunkServerDetails> mockChunkServerDetails = getMockChunkServerDetails();
        UUID[] mockIds = new UUID[]{UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};

        when(statusService.getChunkStatus()).thenReturn(mockChunkServerDetails);
        when(severDetails.getChunkSize()).thenReturn(1024);
        when(severDetails.getMinReplica()).thenReturn(3);
        when(idGenerator.getId()).thenReturn(mockIds[0]).thenReturn(mockIds[1]).thenReturn(mockIds[2]).thenReturn(mockIds[3]);

        UploadRequest request = new UploadRequest();
        request.setFilePath("a.txt");
        request.setFileSize(4096);
        UploadResponse[] uploadResponse = uploadService.getUploadResponse(request);

        assertEquals(4, uploadResponse.length);

        assertEquals(1024, uploadResponse[0].getChunk().getChunkSize());
        List<String> firstChunkUrls = uploadResponse[0].getUrls();
        assertEquals(3, firstChunkUrls.size());
        assertEquals("chunk1", firstChunkUrls.get(0));
        assertEquals("chunk2", firstChunkUrls.get(1));
        assertEquals("chunk3", firstChunkUrls.get(2));

        assertEquals(1024, uploadResponse[1].getChunk().getChunkSize());
        List<String> secondChunkUrls = uploadResponse[1].getUrls();
        assertEquals(3, secondChunkUrls.size());
        assertEquals("chunk4", secondChunkUrls.get(0));
        assertEquals("chunk1", secondChunkUrls.get(1));
        assertEquals("chunk2", secondChunkUrls.get(2));

        assertEquals(1024, uploadResponse[2].getChunk().getChunkSize());
        List<String> thirdChunkUrls = uploadResponse[2].getUrls();
        assertEquals(3, thirdChunkUrls.size());
        assertEquals("chunk3", thirdChunkUrls.get(0));
        assertEquals("chunk4", thirdChunkUrls.get(1));
        assertEquals("chunk1", thirdChunkUrls.get(2));

        assertEquals(1024, uploadResponse[3].getChunk().getChunkSize());
        List<String> fourthChunkUrls = uploadResponse[3].getUrls();
        assertEquals(3, fourthChunkUrls.size());
        assertEquals("chunk2", fourthChunkUrls.get(0));
        assertEquals("chunk3", fourthChunkUrls.get(1));
        assertEquals("chunk4", fourthChunkUrls.get(2));

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
}
