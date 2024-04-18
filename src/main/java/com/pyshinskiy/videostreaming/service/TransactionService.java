package com.pyshinskiy.videostreaming.service;

import com.pyshinskiy.videostreaming.entity.Transaction;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TransactionService {
    Transaction saveTransaction(Transaction transaction);
    Transaction getTransactionById(Long id);
    List<Transaction> getAllTransactions();
    // Other methods if needed
}
