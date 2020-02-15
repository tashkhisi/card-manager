package com.digipay.cardmanager.service;


import com.digipay.cardmanager.component.CardTransactionManager;
import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.model.TransactionResponse;
import com.digipay.cardmanager.repository.TransactionItemRepository;

public  class TransactionGroupServiceResponseHandler implements ResponseHandler {


    private CardTransactionManager cardTransactionManager;

    private TransactionItemRepository transactionItemRepository;


    public TransactionGroupServiceResponseHandler(
            CardTransactionManager cardTransactionManager,
            TransactionItemRepository transactionItemRepository){
        this.cardTransactionManager = cardTransactionManager;
        this.transactionItemRepository =  transactionItemRepository;
    }

    @Override
    public void onFailed(final TransactionResponse response) {
        cardTransactionManager.addAmountToCard(
                response.getTransaction().getCard().getId(),
                response.getTransaction().getAmount());
        TransactionItem item = response.getTransaction();
        if(item.getId() ==null){
            System.out.println("here");
        }
        item.setStatus(TransactionItem.TransactionStatus.FAILED);
        try{
            transactionItemRepository.save(item);
        }catch (Exception ex){
            System.out.println("here");
        }
    }

    @Override
    public void onSuccess(final TransactionResponse response) {
        TransactionItem item = response.getTransaction();
        if(item.getId() ==null){
            System.out.println("here");
        }
        item.setStatus(TransactionItem.TransactionStatus.SUCCEED);
        try{
            transactionItemRepository.save(item);
        }catch (Exception ex){
            System.out.println("here");
        }
    }

    public void onInterrupt(final TransactionResponse response){
        TransactionItem item = response.getTransaction();
        if(item.getId() ==null){
            System.out.println("here");
        }
        item.setStatus(TransactionItem.TransactionStatus.CONTRADICTION);
        try{
            transactionItemRepository.save(item);
        }catch (Exception ex){
            System.out.println("here");
        }

    }
}