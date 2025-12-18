package com.bankx.transactions_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bankx.transactions_service.model.Transactions;

import reactor.core.publisher.Sinks;

@Configuration 
public class SinkConfig { 
    @Bean 
    public Sinks.Many<Transactions> txSink() { 
        return Sinks.many().multicast().onBackpressureBuffer();
    } 
}
