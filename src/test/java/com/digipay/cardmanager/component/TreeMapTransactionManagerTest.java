package com.digipay.cardmanager.component;

import com.digipay.cardmanager.core.util.SimpleMultiValueTree;
import com.digipay.cardmanager.model.Card;
import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.repository.CardRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class TreeMapTransactionManagerTest {

    @Mock
    private SimpleMultiValueTree<Long, Long, Card> simpleMultiValueTree;
    @Mock
    private CardRepository cardRepository;
    private TreeMapTransactionManager treeMapTransactionManager;

    @Captor
    private ArgumentCaptor<Long> keyCaptor;
    private Long totalAmount;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        this.treeMapTransactionManager = spy(new TreeMapTransactionManager(simpleMultiValueTree, cardRepository));
        totalAmount = 2000000L;
    }

    @Test
    public void testCeilingExistRemoveCeilingCalledOnce(){
        doReturn(true).when(treeMapTransactionManager).isTransferPossible(anyLong());
        doAnswer(invocationOnMock ->
            new Card(1L,   (Long) invocationOnMock.getArguments()[0])
        ).when(simpleMultiValueTree).removeCeiling(anyLong());
        given(treeMapTransactionManager.isTransferPossible(anyLong())).willReturn(Boolean.TRUE);
        List<TransactionItem> transactionItems =
                treeMapTransactionManager.getTransactions(totalAmount);
        verify(simpleMultiValueTree, atLeastOnce()).removeCeiling(totalAmount);
    }

    @Test
    public void testCeilingNotExistRemovedLastCalled(){
        doReturn(true).when(treeMapTransactionManager).isTransferPossible(anyLong());
        doAnswer(invocationOnMock ->
                null
        ).when(simpleMultiValueTree).removeCeiling(anyLong());
        doAnswer(invocationOnMock ->
                new Card(1L, totalAmount)
        ).when(simpleMultiValueTree).removeLast();
        List<TransactionItem> transactionItems =
                treeMapTransactionManager.getTransactions(4000000L);
        verify(simpleMultiValueTree, atLeastOnce()).removeLast();
    }

    @Test
    public void testCeilingNotExistRemovedLastNotExistReturnNull(){
        doReturn(true).when(treeMapTransactionManager).isTransferPossible(anyLong());
        doAnswer(invocationOnMock ->
                null
        ).when(simpleMultiValueTree).removeCeiling(anyLong());
        doAnswer(invocationOnMock ->
                null
        ).when(simpleMultiValueTree).removeLast();
        given(treeMapTransactionManager.isTransferPossible(anyLong())).willReturn(Boolean.TRUE);
        assertThrows(IllegalStateException.class, () ->
                treeMapTransactionManager.getTransactions(totalAmount));
    }

    @Test
    public void testCeilingLowerThanTotalAmountCeilingWillBeCalledWithSubtract(){
        Long totalAmount = 2000000L;
        doReturn(true).when(treeMapTransactionManager).isTransferPossible(anyLong());
        Long ceilingCardTransferableAmountShortage = 230000L;
        doAnswer(invocationOnMock ->
                new Card(1L, totalAmount - ceilingCardTransferableAmountShortage)
        ).when(simpleMultiValueTree).removeCeiling(totalAmount);
        doAnswer(invocationOnMock ->
                new Card(1L, totalAmount)
        ).when(simpleMultiValueTree).removeLast();
        List<TransactionItem> transactionItems =
                treeMapTransactionManager.getTransactions(totalAmount);
        verify(simpleMultiValueTree, times(2)).removeCeiling(keyCaptor.capture());
        List<Long> keys = keyCaptor.getAllValues();
        assertEquals(totalAmount, keys.get(0));
        assertEquals(ceilingCardTransferableAmountShortage, keys.get(1));
    }

    @Test
    public void testCeilingLowerThanTotalAmountThenCeilingCalledWithSubtractReturnNullRemoveLastWillBeCalled(){
        doReturn(true).when(treeMapTransactionManager).isTransferPossible(anyLong());
        int count = 0;
        Long ceilingCardTransferableAmountShortage = 230000L;
        doAnswer(invocationOnMock ->{

            if(invocationOnMock.getArguments()[0].equals(totalAmount)){
                return new Card(1L, totalAmount - ceilingCardTransferableAmountShortage);
            }
            else
                return new Card(1L, totalAmount);
        }).when(simpleMultiValueTree).removeCeiling(anyLong());

        List<TransactionItem> transactionItems =
                treeMapTransactionManager.getTransactions(totalAmount);
        verify(simpleMultiValueTree, times(2)).removeCeiling(keyCaptor.capture());
        List<Long> keys = keyCaptor.getAllValues();
        assertEquals(totalAmount, keys.get(0));
        assertEquals(ceilingCardTransferableAmountShortage, keys.get(1));
    }

}
