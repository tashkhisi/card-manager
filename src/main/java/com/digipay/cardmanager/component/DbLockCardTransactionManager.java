package com.digipay.cardmanager.component;

import com.digipay.cardmanager.model.Card;
import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DbLockCardTransactionManager implements CardTransactionManager{

    private CardRepository cardRepository;

    public DbLockCardTransactionManager(CardRepository cardRepository){
        this.cardRepository = cardRepository;
    }
    @Override
    @Transactional
    public synchronized List<TransactionItem> getTransactions(long totalAmount) {
        long sum = 0;
        TransactionItem transaction;
        List<TransactionItem> transferTransactionItems = new ArrayList<>(10);
        do {
            Card card = cardRepository.ceiling(totalAmount - sum, 1);
            if(card == null){
                card = cardRepository.findFirstByOrderByTransferableAmountDesc();
            }
            if(card == null)
                throw new IllegalStateException(
                        "more than total Balance, currentSumTransferableAmount is: "
                                + cardRepository.sum());
            if(card.getTransferableAmount() < 10000)
                throw new IllegalStateException("no card exist");
            Card newCard;
            if (totalAmount - sum <= card.getTransferableAmount()) {

                newCard = new Card(card.getId(),
                        card.getTransferableAmount() - (totalAmount - sum));
                transaction = new TransactionItem(card,
                        totalAmount - sum);
                sum = totalAmount;
            } else {
                transaction = new TransactionItem(card,
                        card.getTransferableAmount());
                sum = card.getTransferableAmount() + sum;
                newCard = new Card(0L);
            }
            cardRepository.save(newCard);
            transferTransactionItems.add(transaction);
        }while(sum != totalAmount);
        return transferTransactionItems;
    }
    protected Card calculateFromCeilingCard(long amount){
        return null;
    }

    @Override
    public boolean isTransferPossible(long totalAmount) {
        return true;
    }

    @Override
    public void addAmountToCard(long cardId, long amount) {
        Optional<Card> dbCard = cardRepository.findById(cardId);
        Card card = dbCard.get();
        Card newCard = new Card(card.getTransferableAmount() + amount);
        cardRepository.save(newCard);
    }

    @Override
    public void initialize(Iterable<Card> cards) {

    }
}
