package com.pyshinskiy.videostreaming.controller;

import com.pyshinskiy.videostreaming.controller.constants.HttpConstants;
import com.pyshinskiy.videostreaming.entity.Transaction;
import com.pyshinskiy.videostreaming.service.TransactionService;
import com.pyshinskiy.videostreaming.service.TransactionServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.pyshinskiy.videostreaming.service.DefaultVideoService;
import com.pyshinskiy.videostreaming.service.VideoService;
import com.pyshinskiy.videostreaming.util.Range;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.*;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/streaming")
public class VideoController {

    private final VideoService videoService;
    private final TransactionService transactionService;


    @Value("${photon.streaming.default-chunk-size}")
    private Integer defaultChunkSize;

    /*@PostMapping
        public ResponseEntity<UUID> save(@RequestParam("file") MultipartFile file) {
            UUID fileUuid = videoService.save(file);
            return ResponseEntity.ok(fileUuid);
        }*/
    @PostMapping
    public ResponseEntity<List<UUID>> save(@RequestParam("files") List<MultipartFile> files, @RequestParam("transaction_id") Long transactionId) {
        return ResponseEntity.ok(videoService.save(files, transactionService.getTransactionById(transactionId)));

    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Resource> fetchChunk(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range,
            @PathVariable UUID uuid) {
        Range parsedRange = Range.parseHttpRangeString(range, defaultChunkSize);
        DefaultVideoService.ChunkWithMetadata chunkWithMetadata = videoService.fetchChunk(uuid, parsedRange);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(CONTENT_TYPE, chunkWithMetadata.metadata().getHttpContentType())
                .header(ACCEPT_RANGES, HttpConstants.ACCEPTS_RANGES_VALUE)
                .header(CONTENT_LENGTH, calculateContentLengthHeader(parsedRange, chunkWithMetadata.metadata().getSize()))
                .header(CONTENT_RANGE, constructContentRangeHeader(parsedRange, chunkWithMetadata.metadata().getSize()))
                .body(chunkWithMetadata.chunk());
    }



    private String calculateContentLengthHeader(Range range, long fileSize) {
        return String.valueOf(range.getRangeEnd(fileSize) - range.getRangeStart() + 1);
    }

    private String constructContentRangeHeader(Range range, long fileSize) {
        return  "bytes " + range.getRangeStart() + "-" + range.getRangeEnd(fileSize) + "/" + fileSize;
    }



   /* private final ResourceLoader resourceLoader;
    @GetMapping("/combined-video")
    public ResponseEntity<Object> getCombinedVideos(HttpServletResponse response) {
        try {
            // Load the two video resources
            Resource video1 = resourceLoader.getResource("classpath:videos/video1.mp4");
            Resource video2 = resourceLoader.getResource("classpath:videos/video2.mp4");

            // Set response content type as video/mp4
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=\"combined_videos.mp4\"");

            // Combine videos
            OutputStream outputStream = response.getOutputStream();
            InputStream video1Stream = video1.getInputStream();
            InputStream video2Stream = video2.getInputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = video1Stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            while ((bytesRead = video2Stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            video1Stream.close();
            video2Stream.close();
            outputStream.flush();

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to combine videos: " + e.getMessage());
        }
    }*/
}
