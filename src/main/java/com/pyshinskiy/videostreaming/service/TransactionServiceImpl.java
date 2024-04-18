package com.pyshinskiy.videostreaming.service;

import com.pyshinskiy.videostreaming.entity.FileMetadataEntity;
import com.pyshinskiy.videostreaming.entity.Transaction;
import com.pyshinskiy.videostreaming.repository.FileMetadataRepository;
import com.pyshinskiy.videostreaming.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;


    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransactionById(Long id) {

        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

   /* public List<FileMetadataEntity> createNestedEntities(List<FileMetadataEntity> nestedEntities) {
        List<FileMetadataEntity> entitiesToUpdate = new ArrayList<>();

        if (!nestedEntities.isEmpty()) {
            List<String> idsToFetch = new ArrayList<>();

            // Collect IDs of nested entities
            for (FileMetadataEntity nestedEntity : nestedEntities) {
                if (nestedEntity.getId() != null) {
                    idsToFetch.add(nestedEntity.getId());
                } else {
                    // If nested entity doesn't have an ID, add it directly
                    entitiesToUpdate.add(nestedEntity);
                }
            }
            // Batch fetch entities by their IDs
            if (!idsToFetch.isEmpty()) {
                try {
                    List<FileMetadataEntity> dbEntities = videoService.findAllByIds(idsToFetch);
                    entitiesToUpdate.addAll(dbEntities);
                } catch (EntityNotFoundException e) {
                    // Handle exception
                }
            }
        }

        return entitiesToUpdate;
    }*/

    // Other methods implementation if needed
}
