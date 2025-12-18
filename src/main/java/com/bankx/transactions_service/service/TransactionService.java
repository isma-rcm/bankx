package com.bankx.transactions_service.service;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import com.bankx.transactions_service.dto.CreateTxRequest;
import com.bankx.transactions_service.exception.BusinessException;
import com.bankx.transactions_service.model.Accounts;
import com.bankx.transactions_service.model.Transactions;
import com.bankx.transactions_service.repository.AccountRepository;
import com.bankx.transactions_service.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;
    private final RiskService riskService;
    private final Sinks.Many<Transactions> txSink;

    public Mono<Transactions> create(CreateTxRequest req) { 
        return accountRepo.findByNumber(req.getAccountNumber()) 
            .switchIfEmpty(Mono.error(new BusinessException("account_not_found")))
            .flatMap(acc -> validateAndApply(acc, req));
    } 

    private Mono<Transactions> validateAndApply(Accounts acc, CreateTxRequest req) { 
        String type = req.getType().toUpperCase();
        BigDecimal amount = req.getAmount(); 

        // 1) Consulta de Riesgo (JPA envuelto en boundedElastic)
        return riskService.isAllowed(acc.getCurrency(), type, amount) 
            .flatMap(allowed -> { 
                if (!allowed) return Mono.error(new BusinessException("risk_rejected"));
                
                // 2) Reglas de negocio: Saldo suficiente
                if ("DEBIT".equals(type) && acc.getBalance().compareTo(amount) < 0) { 
                    return Mono.error(new BusinessException("insufficient_funds"));
                } 

                 // 3) Actualiza balance (CPU-light, podemos publishOn paralelo si deseamos)
                return Mono.just(acc).publishOn(Schedulers.parallel()) 
                    .map(a -> { 
                        BigDecimal newBal = "DEBIT".equals(type) ? 
                            a.getBalance().subtract(amount) : a.getBalance().add(amount); 
                        a.setBalance(newBal); 
                        return a; 
                    }) 
                    .flatMap(accountRepo::save)
                    // 4) Persistir transacción en Mongo
                    .flatMap(saved -> txRepo.save(Transactions.builder() 
                        .accountId(saved.getId()) 
                        .type(type) 
                        .amount(amount) 
                        .timestamp(Instant.now()) 
                        .status("OK") 
                        .build())) 
                    // 5) Notificar por SSE al Sink
                    .doOnNext(tx -> txSink.tryEmitNext(tx));
            }); 
    } 

    // Listado reactivo por cuenta
    public Flux<Transactions> byAccount(String accountNumber) { 
        return accountRepo.findByNumber(accountNumber) 
            .switchIfEmpty(Mono.error(new BusinessException("account_not_found"))) 
            .flatMapMany(acc -> txRepo.findByAccountIdOrderByTimestampDesc(acc.getId()));
    } 

    // Stream de eventos SSE
    public Flux<ServerSentEvent<Transactions>> stream() { 
        return txSink.asFlux() 
            .map(tx -> ServerSentEvent.builder(tx).event("transaction").build()); 
    }
}
