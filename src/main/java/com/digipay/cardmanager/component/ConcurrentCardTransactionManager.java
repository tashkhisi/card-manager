package com.digipay.cardmanager.component;

import com.digipay.cardmanager.core.util.ConcurrentMultiValueTree;
import com.digipay.cardmanager.model.Card;
import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.repository.CardRepository;

import java.util.ArrayList;
import java.util.List;

public class ConcurrentCardTransactionManager implements CardTransactionManager {

    private long sumTransferableAmount;
    private ConcurrentMultiValueTree concurrentMultiValueTree;

    public ConcurrentCardTransactionManager(
            ConcurrentMultiValueTree concurrentMultiValueTree){
        this.concurrentMultiValueTree = concurrentMultiValueTree;
    }
    @Override
    public List<TransactionItem> getTransactions(long totalAmount) {
        long sum =0;
        TransactionItem transaction;
        List<TransactionItem> transferTransactionItems = new ArrayList<>(10);
//        if(!isTransferPossible(totalAmount))
//            throw new IllegalStateException(
//                    "more than total Balance, currentSumTransferableAmount is: "
//                            + sumTransferableAmount);
        do {
            Card card = concurrentMultiValueTree.removeCeiling(totalAmount - sum);
            if(card == null)
                card = concurrentMultiValueTree.removeLast();
            if(card == null)
                throw new IllegalStateException(
                        "more than total Balance, currentSumTransferableAmount is: "
                                + sumTransferableAmount);
//            if(card.getTransferableAmount() < 10000)
//                throw new IllegalStateException("no card exist");
            long cardAmount;
            if (totalAmount - sum <= card.getTransferableAmount()) {
                transaction = new TransactionItem(card,
                        totalAmount - sum);
                cardAmount = card.getTransferableAmount() - (totalAmount - sum);
                sum = totalAmount;
                if (cardAmount >= 10000)
                    concurrentMultiValueTree.addAmountToCard(card.getId(), cardAmount);
                else
                    sumTransferableAmount -= card.getTransferableAmount();
            } else {
                transaction = new TransactionItem(card,
                        card.getTransferableAmount());
                sum = card.getTransferableAmount() + sum;
                cardAmount = 0;
            }
            transferTransactionItems.add(transaction);
            sumTransferableAmount -= transaction.getAmount();
        }while(sum != totalAmount);
        return transferTransactionItems;
    }

    @Override
    public boolean isTransferPossible(long totalAmount) {
        return totalAmount  <= sumTransferableAmount;
    }

    @Override
    public void addAmountToCard(long cardId, long amount) {
        concurrentMultiValueTree.addAmountToCard(cardId, amount);
    }

    @Override
    public void initialize(Iterable<Card> cards) {
        cards.forEach(c ->
                concurrentMultiValueTree.addAmountToCard(c.getId(), c.getTransferableAmount()));
    }
}
