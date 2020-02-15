package com.digipay.cardmanager.service;

import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.transferhelper.NonBlockingCommunicationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TransactionGroupMediator {

    private NonBlockingCommunicationTemplate nonBlockingCommunicationTemplate;

    private TransactionGroupService transactionGroupService;

    AtomicBoolean blockedTime = new AtomicBoolean();

    @PostConstruct
    public void setup(){
        transactionGroupService.initialize();
    }

    @Autowired
    public TransactionGroupMediator(
            NonBlockingCommunicationTemplate nonBlockingCommunicationTemplate,
            TransactionGroupService transactionGroupService){
        this.nonBlockingCommunicationTemplate = nonBlockingCommunicationTemplate;
        this.transactionGroupService = transactionGroupService;
    }


    @Scheduled(cron = "0 30 23 * * *")
    public void scheduleTaskForBlockingComingRequest() {
        blockedTime.set(true);
        nonBlockingCommunicationTemplate.shutDown();
    }

    @Scheduled(cron = "0 50 23 * * *")
    public void scheduleTaskForKillAllThreads() {
        List<TransactionItem> transactionItems = nonBlockingCommunicationTemplate.shutDownNow();
        transactionGroupService.saveAllTransactionItem(transactionItems);
    }

    @Scheduled(cron = "0 05 0 * * *")
    public void scheduleForResetConstraints() {
        nonBlockingCommunicationTemplate.awaitTermination();
        transactionGroupService.initialize();
        blockedTime.set(false);
    }

    public boolean isBlockedTime() {
        return blockedTime.get();
    }
}
