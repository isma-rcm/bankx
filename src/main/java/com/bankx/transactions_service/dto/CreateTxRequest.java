package com.bankx.transactions_service.dto;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data 
public class CreateTxRequest { 
    @NotBlank 
    private String accountNumber;
    
    @NotBlank 
    private String type; // CREDIT/DEBIT
    
    @NotNull 
    @DecimalMin("0.01") 
    private BigDecimal amount; 
}