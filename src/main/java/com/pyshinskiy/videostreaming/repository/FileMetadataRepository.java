package com.pyshinskiy.videostreaming.repository;

import com.pyshinskiy.videostreaming.entity.FileMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface FileMetadataRepository extends JpaRepository<FileMetadataEntity, String> {
    List<FileMetadataEntity> findAllByIdIn(List<String> ids);
}
