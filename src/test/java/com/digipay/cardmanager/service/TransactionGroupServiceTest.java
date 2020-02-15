package com.digipay.cardmanager.service;

import com.digipay.cardmanager.component.CardTransactionManager;
import com.digipay.cardmanager.exception.OutOfResponseException;
import com.digipay.cardmanager.model.Card;
import com.digipay.cardmanager.model.TransactionGroup;
import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.repository.CardRepository;
import com.digipay.cardmanager.repository.TransactionGroupRepository;
import com.digipay.cardmanager.repository.TransactionItemRepository;
import com.digipay.cardmanager.transferhelper.NonBlockingCommunicationTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class TransactionGroupServiceTest {

    private TransactionGroupService transactionGroupService;

    @Mock
    CardRepository cardRepository;

    @Mock
    TransactionItemRepository transactionItemRepository;

    @Mock
    TransactionGroupRepository transactionGroupRepository;

    @Mock
    NonBlockingCommunicationTemplate<TransactionItem> nonBlockingCommunicationTemplate;

    @Mock
    CardTransactionManager cardTransactionManager;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        transactionGroupService = spy(new TransactionGroupService(
                cardTransactionManager, cardRepository,
                nonBlockingCommunicationTemplate,
                transactionItemRepository, transactionGroupRepository));
    }

    @Test
    public void testRequestAtBlockTimeThrowException(){
        given(transactionGroupService.isBlocked()).willReturn(true);
        assertThrows(OutOfResponseException.class, () ->
            transactionGroupService.getTransactions(4000L));
    }

    @Test
    public void testGetTransactionWillCallNonBlockingTemplateDoForEntity(){
        given(transactionGroupService.isBlocked()).willReturn(false);
        List<TransactionItem> list = Arrays.asList(
                new TransactionItem(new Card(1L, 5000L), 40000L));
        given(cardTransactionManager.getTransactions(anyLong()))
                .willReturn(list);
        transactionGroupService.getTransactions(anyLong());
        verify(nonBlockingCommunicationTemplate, times(1)).doForEntity(list);
    }

    @Test
    public void testGetTransactionSaveCallTransactionGroupRepositorySave(){
        given(transactionGroupService.isBlocked()).willReturn(false);
        List<TransactionItem> list = Arrays.asList(
                new TransactionItem(new Card(1L, 5000L), 40000L));
        given(cardTransactionManager.getTransactions(anyLong()))
                .willReturn(list);
        TransactionGroup transactionGroup = transactionGroupService.getTransactions(anyLong());
        verify(transactionGroupRepository, times(1)).save(transactionGroup);
    }

}
