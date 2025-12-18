package com.bankx.transactions_service.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bankx.transactions_service.model.Transactions;

import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveMongoRepository<Transactions, String> {
    Flux<Transactions> findByAccountIdOrderByTimestampDesc(String accountId);
}
