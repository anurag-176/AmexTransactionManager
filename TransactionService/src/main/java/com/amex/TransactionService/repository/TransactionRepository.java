package com.amex.TransactionService.repository;

import com.amex.TransactionService.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByAccountId(Long accountId);
}