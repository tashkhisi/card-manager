package com.digipay.cardmanager.redis;

import com.digipay.cardmanager.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedisCardHelper {
    private static final String TABLE_KEY = "Card";

    @Autowired
    private StringRedisTemplate redisTemplate;

    Map<Long, Card> cardHashMap = new HashMap<>();

    long sumTransferableAmount;

    public Card removeCeiling(long amount){
        return null;
    }
    public Card removeLast(){
        return null;
    }

    public void addCard(Card card){
        cardHashMap.put(card.getId(), card);
        Set<String> rateSet = redisTemplate.opsForZSet().reverseRangeByScore(
                TABLE_KEY, Double.NEGATIVE_INFINITY, card.getPriority());
        // Retrieved the first value since we are expecting only one row
        String str = rateSet.iterator().next();
        redisTemplate.opsForZSet().add(TABLE_KEY, str.toString(), new Double(3000000));

    }
}
