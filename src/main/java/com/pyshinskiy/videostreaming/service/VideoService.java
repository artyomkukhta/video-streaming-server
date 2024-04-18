package com.pyshinskiy.videostreaming.service;

import com.pyshinskiy.videostreaming.entity.FileMetadataEntity;
import com.pyshinskiy.videostreaming.entity.Transaction;
import org.springframework.web.multipart.MultipartFile;
import com.pyshinskiy.videostreaming.util.Range;

import java.util.List;
import java.util.UUID;

public interface VideoService {


    List<UUID> save(List<MultipartFile> video, Transaction transaction);

    DefaultVideoService.ChunkWithMetadata fetchChunk(UUID uuid, Range range);

    List<FileMetadataEntity> findAllByIds(List<String> idsToFetch);
}
