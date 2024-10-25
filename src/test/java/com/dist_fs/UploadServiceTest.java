package com.dist_fs;

import com.dist_fs.beans.ChunkServerDetails;
import com.dist_fs.beans.ServerDetails;
import com.dist_fs.beans.UploadRequest;
import com.dist_fs.beans.UploadResponse;
import com.dist_fs.services.StatusCheckService;
import com.dist_fs.services.UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UploadServiceTest {
    @InjectMocks
    private UploadService uploadService;

    @Mock
    private StatusCheckService statusService;

    @Mock
    private ServerDetails severDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    public void testFileChunking3Chunks() {
        LinkedHashMap<String, ChunkServerDetails> mockChunkServerDetails = getMockChunkServerDetails();

        when(statusService.getChunkStatus()).thenReturn(mockChunkServerDetails);
        when(severDetails.getChunkSize()).thenReturn(1024);
        when(severDetails.getMinReplica()).thenReturn(3);

        UploadRequest request = new UploadRequest();
        request.setFilePath("a.txt");
        request.setFileSize(2093);
        List<UploadResponse> uploadResponse = uploadService.getChunks(request);

        assertEquals(3, uploadResponse.size());

        assertEquals(1024, uploadResponse.get(0).getChunkSize());
        List<String> firstChunkUrls = uploadResponse.get(0).getUrls();
        assertEquals(3, firstChunkUrls.size());
        assertEquals("chunk1", firstChunkUrls.get(0));
        assertEquals("chunk2", firstChunkUrls.get(1));
        assertEquals("chunk3", firstChunkUrls.get(2));

        assertEquals(1024, uploadResponse.get(1).getChunkSize());
        List<String> secondChunkUrls = uploadResponse.get(1).getUrls();
        assertEquals(3, secondChunkUrls.size());
        assertEquals("chunk4", secondChunkUrls.get(0));
        assertEquals("chunk1", secondChunkUrls.get(1));
        assertEquals("chunk2", secondChunkUrls.get(2));

        assertEquals(45, uploadResponse.get(2).getChunkSize());
        List<String> thirdChunkUrls = uploadResponse.get(2).getUrls();
        assertEquals(3, thirdChunkUrls.size());
        assertEquals("chunk3", thirdChunkUrls.get(0));
        assertEquals("chunk4", thirdChunkUrls.get(1));
        assertEquals("chunk1", thirdChunkUrls.get(2));
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
        List<UploadResponse> uploadResponse = uploadService.getChunks(request);

        assertEquals(1, uploadResponse.size());

        assertEquals(1024, uploadResponse.get(0).getChunkSize());
        List<String> firstChunkUrls = uploadResponse.get(0).getUrls();
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
        List<UploadResponse> uploadResponse = uploadService.getChunks(request);

        assertEquals(1, uploadResponse.size());

        assertEquals(123, uploadResponse.get(0).getChunkSize());
        List<String> firstChunkUrls = uploadResponse.get(0).getUrls();
        assertEquals(3, firstChunkUrls.size());
        assertEquals("chunk1", firstChunkUrls.get(0));
        assertEquals("chunk2", firstChunkUrls.get(1));
        assertEquals("chunk3", firstChunkUrls.get(2));

    }

    @Test
    public void testFileChunkingExact4Chunks() {
        LinkedHashMap<String, ChunkServerDetails> mockChunkServerDetails = getMockChunkServerDetails();

        when(statusService.getChunkStatus()).thenReturn(mockChunkServerDetails);
        when(severDetails.getChunkSize()).thenReturn(1024);
        when(severDetails.getMinReplica()).thenReturn(3);

        UploadRequest request = new UploadRequest();
        request.setFilePath("a.txt");
        request.setFileSize(4096);
        List<UploadResponse> uploadResponse = uploadService.getChunks(request);

        assertEquals(4, uploadResponse.size());

        assertEquals(1024, uploadResponse.get(0).getChunkSize());
        List<String> firstChunkUrls = uploadResponse.get(0).getUrls();
        assertEquals(3, firstChunkUrls.size());
        assertEquals("chunk1", firstChunkUrls.get(0));
        assertEquals("chunk2", firstChunkUrls.get(1));
        assertEquals("chunk3", firstChunkUrls.get(2));

        assertEquals(1024, uploadResponse.get(1).getChunkSize());
        List<String> secondChunkUrls = uploadResponse.get(1).getUrls();
        assertEquals(3, secondChunkUrls.size());
        assertEquals("chunk4", secondChunkUrls.get(0));
        assertEquals("chunk1", secondChunkUrls.get(1));
        assertEquals("chunk2", secondChunkUrls.get(2));

        assertEquals(1024, uploadResponse.get(2).getChunkSize());
        List<String> thirdChunkUrls = uploadResponse.get(2).getUrls();
        assertEquals(3, thirdChunkUrls.size());
        assertEquals("chunk3", thirdChunkUrls.get(0));
        assertEquals("chunk4", thirdChunkUrls.get(1));
        assertEquals("chunk1", thirdChunkUrls.get(2));

        assertEquals(1024, uploadResponse.get(3).getChunkSize());
        List<String> fourthChunkUrls = uploadResponse.get(3).getUrls();
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
