package com.bankx.transactions_service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bankx.transactions_service.service.RiskService;

import reactor.test.StepVerifier;

@SpringBootTest
class RiskServiceTest {
    @Autowired 
    private RiskService riskService;

    @Test
    void allowsDebitUnderLimit() {
        StepVerifier.create(riskService.isAllowed("PEN", "DEBIT", new BigDecimal("100")))
            .expectNext(true)
            .verifyComplete();
    }
}
