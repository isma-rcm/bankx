package com.bankx.transactions_service.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bankx.transactions_service.model.Accounts;
import com.bankx.transactions_service.model.RiskRule;
import com.bankx.transactions_service.repository.AccountRepository;
import com.bankx.transactions_service.repository.RiskRuleRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final RiskRuleRepository riskRepo;
    private final AccountRepository accountRepo;

    @Override 
    public void run(String... args) {
        // Bloqueante (JPA)
        riskRepo.save(RiskRule.builder().currency("PEN").maxDebitPerTx(new BigDecimal("1500")).build());
        riskRepo.save(RiskRule.builder().currency("USD").maxDebitPerTx(new BigDecimal("500")).build());

        // Reactivo (Mongo) 
        accountRepo.deleteAll()
            .thenMany(Flux.just(
                Accounts.builder().number("001-0001").holderName("Ana Peru").currency("PEN").balance(new BigDecimal("2000")).build(),
                Accounts.builder().number("001-0002").holderName("Luis Acuña").currency("PEN").balance(new BigDecimal("800")).build()
            ))
            .flatMap(accountRepo::save)
            .blockLast();  // solo para seed en arranque
    }
}
