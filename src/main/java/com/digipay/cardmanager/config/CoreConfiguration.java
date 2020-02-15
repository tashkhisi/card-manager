package com.digipay.cardmanager.config;

import com.digipay.cardmanager.component.CardTransactionManager;
import com.digipay.cardmanager.component.ConcurrentCardTransactionManager;
import com.digipay.cardmanager.component.DbLockCardTransactionManager;
import com.digipay.cardmanager.component.DefaultCardTransactionManager;
import com.digipay.cardmanager.core.util.ConcurrentMultiValueTree;
import com.digipay.cardmanager.model.Card;
import com.digipay.cardmanager.core.util.SimpleMultiValueTree;
import com.digipay.cardmanager.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfiguration {

    @Autowired
    private CardRepository cardRepository;
    @Bean
    public CardTransactionManager cardTransactionManager(){
        return new ConcurrentCardTransactionManager(new ConcurrentMultiValueTree());
    }
}
