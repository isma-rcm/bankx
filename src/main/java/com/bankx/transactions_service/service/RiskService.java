package com.bankx.transactions_service.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.bankx.transactions_service.model.RiskRule;
import com.bankx.transactions_service.repository.RiskRuleRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class RiskService {
    private final RiskRuleRepository riskRepo;

    public Mono<Boolean> isAllowed(String currency, String type, BigDecimal amount) {
        return Mono.fromCallable(() -> riskRepo.findFirstByCurrency(currency)
                .map(RiskRule::getMaxDebitPerTx)
                .orElse(new BigDecimal("0")))
                .subscribeOn(Schedulers.boundedElastic()) // bloqueante a elastic
                .map(max -> {
                    if ("DEBIT".equalsIgnoreCase(type)) {
                        return amount.compareTo(max) <= 0;
                    }
                    return true;
                });
    }
}
