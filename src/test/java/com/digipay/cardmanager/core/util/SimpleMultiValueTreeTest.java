package com.digipay.cardmanager.core.util;

import com.digipay.cardmanager.core.util.SimpleMultiValueTree;
import com.digipay.cardmanager.model.Card;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SimpleMultiValueTreeTest {

    private SimpleMultiValueTree<Long, Long, Card> simpleMultiValueTree;
    List<Card> list;

    @Before
    public void setup() {
        this.simpleMultiValueTree = new SimpleMultiValueTree<>();
        list = Arrays.asList(
                new Card(1L, 3000000L),
                new Card(2L, 3000000L),
                new Card(3L, 2000000L),
                new Card(4L, 2000000L),
                new Card(5L, 1000000L),
                new Card(6L, 1000000L));
        list.forEach(a -> simpleMultiValueTree.addElement(a));
    }

    @Test
    public void testAddElement() {
        Card removedCard = simpleMultiValueTree.removeElement(1L);
        assertEquals(list.get(0), removedCard);
    }

    @Test
    public void testRemovedElementReturnNull() {
        Card removedCard = simpleMultiValueTree.removeElement(1L);
        assertEquals(list.get(0), removedCard);
        assertEquals(simpleMultiValueTree.removeElement(1L), null);
    }

    @Test
    public void testRemoveCeiling() {
        Card card = simpleMultiValueTree.removeCeiling(2000000L);
        assertThat(card.getId(), anyOf(is(3L), is(4L)));
        card = simpleMultiValueTree.removeCeiling(2000000L);
        assertThat(card.getId(), anyOf(is(3L), is(4L)));
        card = simpleMultiValueTree.removeCeiling(2000000L);
        assertThat(card.getId(), anyOf(is(1L), is(2L)));
    }

    @Test
    public void testRemoveLast(){
        Card card = simpleMultiValueTree.removeLast();
        assertThat(card.getId(), anyOf(is(1L), is(2L)));
    }


}
