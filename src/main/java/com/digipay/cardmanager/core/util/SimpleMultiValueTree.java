package com.digipay.cardmanager.core.util;

import java.util.*;

public class SimpleMultiValueTree<T, K, M extends MultiValueTreeElement<T, K>> {

    private NavigableMap<K, Map<T, M>> navigableMap = new TreeMap<>();
    private Map<T, K> identityToPriorityMap = new HashMap<>();

    public  M removeElement(T t) {
        K k = identityToPriorityMap.get(t);
        if(k == null)
            return null;
        Map<T, M> cards = navigableMap.get(k);
        M m = cards.remove(t);
        if(cards.size() == 0)
            navigableMap.remove(k);
        identityToPriorityMap.remove(t);
        return m;
    }

    public void addElement(M m){
        if(identityToPriorityMap.containsKey(m.getId()))
            throw new IllegalStateException();
        K k = m.getPriority();
        Map<T, M> map = navigableMap.get(k);
        if(map == null){
            map =  new HashMap<>();
            navigableMap.put(k, map);
        }
        identityToPriorityMap.put(m.getId(), m.getPriority());
        map.put(m.getId(), m);
    }

    public synchronized List<M> toList() {
        List<M> cardList = new ArrayList<>();
        for (K priority : navigableMap.keySet()) {
            Map<T, M> elements = navigableMap.get(priority);
            for (M m : elements.values())
                cardList.add(m);
        }
        return cardList;
    }

    public M removeCeiling(K k) {
        Map.Entry<K, Map<T, M>> ceilingEntry =
                navigableMap.ceilingEntry(k);
        return getFirstOccurrence(ceilingEntry);
    }

    public M removeLast(){
        Map.Entry<K, Map<T, M>> ceilingEntry =
                navigableMap.lastEntry();
        return getFirstOccurrence(ceilingEntry);
    }

    private M getFirstOccurrence(Map.Entry<K, Map<T, M>> ceilingEntry) {
        if (ceilingEntry == null) {
            return null;
        }
        Map<T, M> priorityElement = ceilingEntry.getValue();
        Iterator<T> iterator = priorityElement.keySet().iterator();
        M nextElement = priorityElement.remove(iterator.next());
        identityToPriorityMap.remove(nextElement.getId());
        if (!iterator.hasNext())
            navigableMap.remove(ceilingEntry.getKey());
        return nextElement;
    }

    @Override
    public String toString() {
        return "SimpleMultiValueTree{" +
                "navigableMap=" + navigableMap +
                ", identityToPriorityMap=" + identityToPriorityMap +
                '}';
    }

    //    public Map<T, M> getAllCeilingWithPriority(K k){
//        Map.Entry<K, Map<T, M>> ceilingEntry =
//                navigableMap.ceilingEntry(k);
//        if(ceilingEntry == null){
//            return null;
//        }
//        return ceilingEntry.getValue();
//    }
}
