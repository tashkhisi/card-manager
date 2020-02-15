package com.digipay.cardmanager.transferhelper;

import com.digipay.cardmanager.model.Card;
import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.service.ResponseHandler;
import com.digipay.cardmanager.transferhelper.NonBlockingTransactionExecutionImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NonBlockingTransactionExecutionImplTest {


    private NonBlockingTransactionExecutionImpl nonBlockingTransactionExecution;

    @Mock
    private ExecutorService executorService;

    @Mock
    private ResponseHandler responseHandler;

    @Mock
    private List<TransactionItem> transactionItems;


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        nonBlockingTransactionExecution = new NonBlockingTransactionExecutionImpl(responseHandler, executorService);
        transactionItems = Arrays.asList(
                new TransactionItem(new Card(1L), 2000L),
                new TransactionItem(new Card(1L), 2000L),
                new TransactionItem(new Card(1L), 2000L),
                new TransactionItem(new Card(1L), 2000L));
    }

    @Test
    public void testCallExecutorForEachTransactionItem(){
        nonBlockingTransactionExecution.doForEntity(transactionItems);
        verify(executorService, times(transactionItems.size())).execute(any());
    }

}
