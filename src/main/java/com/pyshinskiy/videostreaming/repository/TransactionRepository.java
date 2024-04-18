package com.pyshinskiy.videostreaming.repository;

import com.pyshinskiy.videostreaming.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // You can add custom query methods here if needed
}
