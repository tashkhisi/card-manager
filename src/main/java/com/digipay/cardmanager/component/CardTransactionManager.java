package com.digipay.cardmanager.component;

import com.digipay.cardmanager.model.Card;
import com.digipay.cardmanager.model.TransactionItem;

import java.util.List;

public interface CardTransactionManager {
    List<TransactionItem> getTransactions(long totalAmount);
    boolean isTransferPossible(long totalAmount);
    void addAmountToCard(long cardId, long amount);
    void initialize(Iterable<Card> cards);
}
