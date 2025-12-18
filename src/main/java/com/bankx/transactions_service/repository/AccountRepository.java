package com.bankx.transactions_service.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bankx.transactions_service.model.Accounts;

import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Accounts,String>{
    Mono<Accounts>findByNumber(String number);    
} 
