package com.digipay.cardmanager.service;

import com.digipay.cardmanager.component.CardTransactionManager;
import com.digipay.cardmanager.exception.OutOfResponseException;
import com.digipay.cardmanager.model.TransactionGroup;
import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.repository.CardRepository;
import com.digipay.cardmanager.repository.TransactionGroupRepository;
import com.digipay.cardmanager.repository.TransactionItemRepository;
import com.digipay.cardmanager.transferhelper.NonBlockingCommunicationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * the main service that is called by controller
 */
@Service()
public class TransactionGroupService {

    private CardRepository cardRepository;

    private NonBlockingCommunicationTemplate<TransactionItem> nonBlockingCommunicationTemplate;

    private TransactionItemRepository transactionItemRepository;

    private TransactionGroupRepository transactionGroupRepository;

    private CardTransactionManager cardTransactionManager;

    private AtomicBoolean blocked = new AtomicBoolean();

    @Autowired
    public TransactionGroupService(
            CardTransactionManager defaultCardTransactionManager,
            CardRepository cardRepository,
            NonBlockingCommunicationTemplate<TransactionItem> nonBlockingCommunicationTemplate,
            TransactionItemRepository transactionItemRepository,
            TransactionGroupRepository transactionGroupRepository){
        this.cardTransactionManager = defaultCardTransactionManager;
        this.cardRepository = cardRepository;
        this.nonBlockingCommunicationTemplate = nonBlockingCommunicationTemplate;
        this.transactionGroupRepository = transactionGroupRepository;
        this.transactionItemRepository = transactionItemRepository;
    }

    public void initialize(){
        cardTransactionManager.initialize(cardRepository.findAll());
    }

    public void saveAllTransactionItem(List<TransactionItem> transactionItems){
        transactionItems.forEach(transactionItem ->
                transactionItem.setStatus(TransactionItem.TransactionStatus.FAILED));
        transactionItemRepository.saveAll(transactionItems);
    }

    public TransactionGroup getTransactions(Long totalAmount){
        if(!isBlocked()){
            List<TransactionItem> transactions = cardTransactionManager.getTransactions(totalAmount);
            TransactionGroup transactionGroup = new TransactionGroup();
            for(int i = 0; i < transactions.size(); i++)
                transactionGroup.addTransaction(transactions.get(i));
            transactionGroupRepository.save(transactionGroup);
            nonBlockingCommunicationTemplate.doForEntity(transactions);
            return transactionGroup;
        }
        else
            throw new OutOfResponseException();
    }

    public void setBlock(boolean blocked){
        this.blocked.set(blocked);
    }

    public boolean isBlocked(){
        return this.blocked.get();
    }

    public TransactionGroup report(Long transactionGroupId){
        return transactionGroupRepository.findById(transactionGroupId).get();
    }

    public List<TransactionGroup> report(){
        return transactionGroupRepository.findAll();
    }


}
