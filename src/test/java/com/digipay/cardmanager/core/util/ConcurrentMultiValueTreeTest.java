package com.digipay.cardmanager.core.util;

import com.digipay.cardmanager.core.util.ConcurrentMultiValueTree;
import com.digipay.cardmanager.model.Card;
import edu.umd.cs.mtc.MultithreadedTest;
import edu.umd.cs.mtc.TestFramework;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentMultiValueTreeTest extends MultithreadedTest {
    private ConcurrentMultiValueTree concurrentMultiValueTree;
    private final Set<Long> idSet = new HashSet<>();

    @Test
    public void test() throws Throwable {
        this.runTest();
    }

    @Override
    public void initialize() {
        concurrentMultiValueTree = new ConcurrentMultiValueTree();
    }

    public void thread1() {
        waitForTick(1);
        concurrentMultiValueTree.addAmountToCard(1L, 10000L);
    }

    public void thread2() {
        waitForTick(1);
        concurrentMultiValueTree.addAmountToCard(1L, 20000L);
        waitForTick(2);
        Card card = concurrentMultiValueTree.removeCard(1L);
        assertTrue(card.getTransferableAmount() == 30000);
        waitForTick(3);
        card = concurrentMultiValueTree.removeCeiling(2000000);
        assertTrue(Arrays.asList(3L, 4L, 5L, 6L).contains(card.getId()));
        System.out.println(card.getId());
        idSet.add(card.getId());
        waitForTick(4);
    }

    public void thread3() {
        waitForTick(2);
        concurrentMultiValueTree.addAmountToCard(1L, 20000L);
        Card card = concurrentMultiValueTree.removeCard(1L);
        assertTrue((card == null || card.getTransferableAmount().equals(20000L)));
        concurrentMultiValueTree.addAmountToCard(3L, 2000000L);
        concurrentMultiValueTree.addAmountToCard(4L, 2000000L);
        concurrentMultiValueTree.addAmountToCard(5L, 2500000L);
        concurrentMultiValueTree.addAmountToCard(6L, 2500000L);
        waitForTick(3);
        card = concurrentMultiValueTree.removeCeiling(2000000);
        assertTrue(Arrays.asList(3L, 4L, 5L, 6L).contains(card.getId()));
        System.out.println(card.getId());
        idSet.add(card.getId());
        waitForTick(4);
    }

    public void thread4() {
        Card card;
        waitForTick(3);
        card = concurrentMultiValueTree.removeCeiling(2000000);
        assertTrue(Arrays.asList(3L, 4L, 5L, 6L).contains(card.getId()));
        System.out.println(card.getId());

        idSet.add(card.getId());
        waitForTick(4);
    }
    public void thread5(){
        Card card;
        waitForTick(3);
        card = concurrentMultiValueTree.removeLast();
        assertTrue(Arrays.asList(5L, 6L).contains(card.getId()));
        idSet.add(card.getId());
        waitForTick(4);
        assertTrue(idSet.containsAll(Arrays.asList(3L, 4L, 5L, 6L)));
    }
}

