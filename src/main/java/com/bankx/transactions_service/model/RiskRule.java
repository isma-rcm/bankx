package com.bankx.transactions_service.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity 
@Table(name = "risk_rules")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class RiskRule {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String currency;
    private BigDecimal maxDebitPerTx;
}
