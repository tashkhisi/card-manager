package com.digipay.cardmanager.component;

import com.digipay.cardmanager.model.Card;
import com.digipay.cardmanager.model.TransactionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;

public class RedisCardTransactionManager implements CardTransactionManager {
    private static final String TABLE_KEY = "Card";

    @Autowired
    private StringRedisTemplate redisTemplate;

    long sumTransferableAmount;


    @Override
    public synchronized List<TransactionItem> getTransactions(long totalAmount) {
        return null;
    }

    @Override
    public boolean isTransferPossible(long totalAmount) {
        return false;
    }

    @Override
    public void addAmountToCard(long cardId, long amount) {

    }

    @Override
    public void initialize(Iterable<Card> cards) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 1000000; i++) {
            str.append(i + ",");
        }
        for(int i = 0; i < 1000000; i++){
            redisTemplate.opsForZSet().add(TABLE_KEY, str.toString(), new Double(3000000));
        }
    }
}
