package com.pyshinskiy.videostreaming.binarystorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    public InputStream getInputStream(UUID uuid, long offset, long length) throws Exception {
        String fileName = uuid.toString() + ".mp4";
        File videoFile = new File(VIDEO_DIRECTORY + fileName);
        FileInputStream fileInputStream = new FileInputStream(videoFile);
        long skipped = fileInputStream.skip(offset);
        if (skipped != offset) {
            throw new IOException("Failed to skip to the specified offset");
        }
        return new BoundedInputStream(fileInputStream, length);
    }

    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf('.');
        return lastIndex == -1 ? "" : filename.substring(lastIndex);
    }

    // Custom InputStream implementation to read only a bounded length of bytes
    private static class BoundedInputStream extends FilterInputStream {
        private long bytesRemaining;

        public BoundedInputStream(InputStream in, long bytesRemaining) {
            super(in);
            this.bytesRemaining = bytesRemaining;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (bytesRemaining <= 0) {
                return -1;
            }
            int bytesRead = super.read(b, off, (int) Math.min(len, bytesRemaining));
            if (bytesRead > 0) {
                bytesRemaining -= bytesRead;
            }
            return bytesRead;
        }

        @Override
        public int read() throws IOException {
            if (bytesRemaining <= 0) {
                return -1;
            }
            int byteRead = super.read();
            if (byteRead != -1) {
                bytesRemaining--;
            }
            return byteRead;
        }
    }
}
