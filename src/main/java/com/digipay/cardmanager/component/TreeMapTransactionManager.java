package com.digipay.cardmanager.component;

import com.digipay.cardmanager.model.Card;
import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.core.util.SimpleMultiValueTree;
import com.digipay.cardmanager.repository.CardRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TreeMapTransactionManager implements CardTransactionManager {

    private final SimpleMultiValueTree<Long, Long, Card> cardMultiValueTree;
    private final CardRepository cardRepository;

    public TreeMapTransactionManager(
            final SimpleMultiValueTree<Long, Long, Card> simpleMultiValueTree, final CardRepository cardRepository){
        this.cardMultiValueTree = simpleMultiValueTree;
        this.cardRepository = cardRepository;
    }

    /**
     * sum transferable amount for all cards in the carMultiValueTree for short circuiting in situation that
     * sum amount of all cards is less than the requested totalAmount, the worst case for this algorithm occurs when
     * the requested amount by the api client is more than the sum of all cards so using this variable we can
     * try to avoid unnecessary calculation in this situation
     */
    private long sumTransferableAmount;

    public synchronized void initialize(Iterable<Card> cards){
        List<Card> result = new ArrayList<>();
        cards.iterator().forEachRemaining(result::add);
        sumTransferableAmount = 0;
        result.forEach(card -> {
            if(card.getTransferableAmount() > 10000){
                sumTransferableAmount += card.getTransferableAmount();
                cardMultiValueTree.addElement(card);
            }
        });
    }

    @Override
    public synchronized List<TransactionItem> getTransactions(long totalAmount){
        long sum = 0;
        TransactionItem transaction;
        List<TransactionItem> transferTransactionItems = new ArrayList<>(10);
        if(!isTransferPossible(totalAmount))
            throw new IllegalStateException(
                    "more than total Balance, currentSumTransferableAmount is: "
                            + sumTransferableAmount);
        do {
            Card card = cardMultiValueTree.removeCeiling(totalAmount - sum);
            if(card == null){
                card = cardMultiValueTree.removeLast();
            }
            if(card == null)
                throw new IllegalStateException(
                        "more than total Balance, currentSumTransferableAmount is: "
                                + sumTransferableAmount);
            Card newCard;
            if (totalAmount - sum <= card.getTransferableAmount()) {
                newCard = new Card(card.getId(), card.getTransferableAmount() - (totalAmount - sum));
                transaction = new TransactionItem(card,
                        totalAmount - sum);
                sum = totalAmount;
                if (card.getTransferableAmount() >= 10000)
                    cardMultiValueTree.addElement(newCard);
                else
                    sumTransferableAmount -= card.getTransferableAmount();
            } else {
                transaction = new TransactionItem(card,
                        card.getTransferableAmount());
                sum = card.getTransferableAmount() + sum;
                newCard = new Card(card.getId(), 0L);
                cardMultiValueTree.addElement(newCard);
            }
            if(cardRepository != null){
//                cardRepository.save(newCard);
            }
            transferTransactionItems.add(transaction);
            sumTransferableAmount -= transaction.getAmount();
        }while(sum != totalAmount);
        return transferTransactionItems;
    }

    @Override
    public synchronized boolean isTransferPossible(long totalAmount) {
        return totalAmount <= sumTransferableAmount;
    }

    @Override
    public synchronized void addAmountToCard(long cardId, long amount){
        Card card = cardMultiValueTree.removeElement(cardId);
        if(card == null){
            Optional<Card> dbCard = cardRepository.findById(cardId);
            if(dbCard.isPresent()){
                card = dbCard.get();
                sumTransferableAmount += card.getTransferableAmount();
            }
            else
                card = new Card(cardId, 0L);
        }
        Card newCard = new Card(card.getId(), card.getTransferableAmount() + amount);
        cardRepository.save(newCard);
        cardMultiValueTree.addElement(newCard);
        sumTransferableAmount += amount;
    }

    public synchronized List<Card> toList(){
        return cardMultiValueTree.toList();
    }
}
