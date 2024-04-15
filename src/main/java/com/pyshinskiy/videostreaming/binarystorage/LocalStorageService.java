package com.pyshinskiy.videostreaming.binarystorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Slf4j
@Service
public class LocalStorageService {
    @Value("${photon.streaming.directory}")
    private String VIDEO_DIRECTORY;

    @Value("${photon.streaming.default-chunk-size}")
    private int CHUNK_SIZE;

    public void save(MultipartFile file, UUID uuid) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String fileName = (originalFilename != null) ? uuid.toString() + getFileExtension(originalFilename) : uuid.toString();
        File targetFile = new File(VIDEO_DIRECTORY + fileName);
        try (InputStream is = file.getInputStream();
             FileOutputStream os = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
        log.info("saved " + fileName + " to " + targetFile.getAbsolutePath());
    }

    public Resource getResource(UUID uuid, long offset, long length) throws IOException {
        String fileName = uuid.toString() + ".mp4";
        File videoFile = new File(VIDEO_DIRECTORY + fileName);
        try (FileInputStream fileInputStream = new FileInputStream(videoFile)) {
            long skipped = fileInputStream.skip(offset);
            if (skipped != offset) {
                throw new IOException("Failed to skip to the specified offset");
            }
            byte[] chunkBytes = new byte[(int) length];
            int bytesRead = fileInputStream.read(chunkBytes);
            if (bytesRead < length) {
                log.warn("Expected to read {} bytes but only {} bytes read", length, bytesRead);
            }
            return new ByteArrayResource(chunkBytes);
        }
    }


    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf('.');
        return lastIndex == -1 ? "" : filename.substring(lastIndex);
    }
}
