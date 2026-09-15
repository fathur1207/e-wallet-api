package com.spring.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.ewallet.model.TransactionLog;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
}
