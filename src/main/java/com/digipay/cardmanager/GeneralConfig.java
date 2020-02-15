package com.digipay.cardmanager;

import com.digipay.cardmanager.component.CardTransactionManager;
import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.repository.TransactionItemRepository;
import com.digipay.cardmanager.service.TransactionGroupServiceResponseHandler;
import com.digipay.cardmanager.service.ResponseHandler;
import com.digipay.cardmanager.transferhelper.NonBlockingCommunicationTemplate;
import com.digipay.cardmanager.transferhelper.NonBlockingTransactionExecutionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;


@Configuration
public class GeneralConfig {

    @Autowired
    private CardTransactionManager cardTransactionManager;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Bean
    public NonBlockingCommunicationTemplate<TransactionItem> nonBlockingCommunicationTemplate(){
        ResponseHandler responseHandler =
                new TransactionGroupServiceResponseHandler(cardTransactionManager, transactionItemRepository);
        return new NonBlockingTransactionExecutionImpl(responseHandler, Executors.newFixedThreadPool(40));
    }
}
