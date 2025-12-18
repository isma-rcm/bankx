package com.bankx.transactions_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankx.transactions_service.dto.CreateTxRequest;
import com.bankx.transactions_service.model.Transactions;
import com.bankx.transactions_service.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService service;

    @PostMapping("/transactions")
    public Mono<ResponseEntity<Transactions>> create(@Valid @RequestBody CreateTxRequest req) {
        return service.create(req)
                .map(t -> ResponseEntity.status(HttpStatus.CREATED).body(t)); 
    }

    @GetMapping("/transactions")
    public Flux<Transactions> list(@RequestParam String accountNumber) {
        return service.byAccount(accountNumber);
    }

    @GetMapping(value = "/stream/transactions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Transactions>> stream() {
        return service.stream();
    }
}
