package com.pyshinskiy.videostreaming.service;

import com.pyshinskiy.videostreaming.binarystorage.LocalStorageService;
import com.pyshinskiy.videostreaming.entity.FileMetadataEntity;
import com.pyshinskiy.videostreaming.exception.StorageException;
import com.pyshinskiy.videostreaming.repository.FileMetadataRepository;
import com.pyshinskiy.videostreaming.util.Range;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultVideoService implements VideoService {

    private final LocalStorageService storageService;
    private final FileMetadataRepository fileMetadataRepository;

    @Override
    @Transactional
    public UUID save(MultipartFile video) {
        try {
            UUID fileUuid = UUID.randomUUID();
            FileMetadataEntity metadata = FileMetadataEntity.builder()
                    .id(fileUuid.toString())
                    .size(video.getSize())
                    .httpContentType(video.getContentType())
                    .build();
            fileMetadataRepository.save(metadata);
            storageService.save(video, fileUuid);
            return fileUuid;
        } catch (Exception ex) {
            log.error("Exception occurred when trying to save the file", ex);
            throw new StorageException(ex);
        }
    }

    @Override
    public ChunkWithMetadata fetchChunk(UUID uuid, Range range) {
        FileMetadataEntity fileMetadata = fileMetadataRepository.findById(uuid.toString()).orElseThrow();
        return new ChunkWithMetadata(fileMetadata, readChunk(uuid, range, fileMetadata.getSize()));
    }

    private Resource readChunk(UUID uuid, Range range, long fileSize) {
        long startPosition = range.getRangeStart();
        long endPosition = range.getRangeEnd(fileSize);
        long chunkSize = endPosition - startPosition + 1;
        try {
            return storageService.getResource(uuid, startPosition, chunkSize);
        } catch (IOException e) {
            throw new RuntimeException(e);//resource not found ex
        }

    }

    public record ChunkWithMetadata(
            FileMetadataEntity metadata,
            Resource chunk
    ) {}
}
