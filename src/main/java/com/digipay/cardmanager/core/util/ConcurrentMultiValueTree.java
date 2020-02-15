package com.digipay.cardmanager.core.util;

import com.digipay.cardmanager.model.Card;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentMultiValueTree {

    private final ConcurrentSkipListMap<Long, Map<Long, Card>>
        map = new ConcurrentSkipListMap<>();
    private final Map<Long, Long> mapIdToPriority = Collections.synchronizedMap(new HashMap<>());
    private final Map<Long, ReentrantLock> cardLocks = new ConcurrentHashMap<>();
    private final Map<Long, Object> priorityLocks = new ConcurrentSkipListMap<>();

    public void addAmountToCard(final Long id, final Long amount){
        cardLocks.putIfAbsent(id, new ReentrantLock());
        priorityLocks.putIfAbsent(amount, new Object());
        cardLocks.get(id).lock();
        Card card = removeCard(id);
        long totalAmount = amount + (card != null ? card.getTransferableAmount() : 0);
        priorityLocks.putIfAbsent(totalAmount, new Object());
        synchronized (priorityLocks.get(totalAmount)) {
            if(totalAmount >= 10000){
                Map<Long, Card> mm = map.computeIfAbsent(totalAmount, a -> {
                    Map<Long, Card> cardMap =
                            Collections.synchronizedMap(new HashMap<>());
                    return cardMap;
                });
                mm.put(id, new Card(id, totalAmount));
                mapIdToPriority.put(id, totalAmount);
            }
        }
        cardLocks.get(id).unlock();
    }

    public Card removeCard(Long id){
        cardLocks.get(id).lock();
        Long priority = mapIdToPriority.remove(id);
        if (priority == null) {
            cardLocks.get(id).unlock();
            return null;
        }
        Map<Long, Card> carMap = map.get(priority);
        Card card;
        synchronized (priorityLocks.get(priority)){
             card = carMap.remove(id);
            if (carMap.size() == 0) {
                map.remove(priority);
            }
        }
        cardLocks.get(id).unlock();
        return card;

    }

    public Card removeLast() {
        boolean cardFind = false;
        Card selectedCard = null;
        while(!cardFind){
            Map.Entry<Long, Map<Long, Card>> cardMap = map.lastEntry();
            if(cardMap == null)
                return null;
            long priority = cardMap.getKey();
            Iterator<Card> cardsIterator;
            synchronized (priorityLocks.get(priority)) {
                cardsIterator = cardMap.getValue().values().iterator();
                if (!cardsIterator.hasNext())
                    map.remove(cardMap.getKey(), cardMap);
                while (cardsIterator.hasNext()) {
                    selectedCard = cardsIterator.next();
                    if (cardLocks.get(selectedCard.getId()).tryLock()) {
                        try{
                            mapIdToPriority.get(selectedCard.getId()).equals(cardMap.getKey());
                        }catch (Exception ex){
                            System.out.println("here");
                        }
                        if (mapIdToPriority.get(selectedCard.getId()).equals(cardMap.getKey())) {
                            cardFind = true;
                            break;
                        } else
                            cardLocks.get(selectedCard.getId()).unlock();
                    }
                }
            }
        }
        removeCard(selectedCard.getId());
        cardLocks.get(selectedCard.getId()).unlock();
        return selectedCard;
    }

    public Card removeCeiling(long amount) {
        boolean cardFind = false;
        Card selectedCard = null;
        while(!cardFind){
            Map.Entry<Long, Map<Long, Card>> cardMap = map.ceilingEntry(amount);
            if(cardMap == null)
                return null;
            long priority = cardMap.getKey();
            Iterator<Card> cardsIterator;
            synchronized (priorityLocks.get(priority)) {
                cardsIterator = cardMap.getValue().values().iterator();
                if (!cardsIterator.hasNext())
                    map.remove(cardMap.getKey(), cardMap);
                while (cardsIterator.hasNext()) {
                    selectedCard = cardsIterator.next();
                    if (cardLocks.get(selectedCard.getId()).tryLock()) {
                        if (mapIdToPriority.get(selectedCard.getId()).equals(cardMap.getKey())) {
                            cardFind = true;
                            break;
                        } else
                            cardLocks.get(selectedCard.getId()).unlock();
                    }
                }
            }
        }
        removeCard(selectedCard.getId());
        cardLocks.get(selectedCard.getId()).unlock();
        return selectedCard;
    }

}
