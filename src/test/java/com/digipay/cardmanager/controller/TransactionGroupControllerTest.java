package com.digipay.cardmanager.controller;

import com.digipay.cardmanager.data.TransactionRequestDTO;
import com.digipay.cardmanager.model.Card;
import com.digipay.cardmanager.model.TransactionGroup;
import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.service.TransactionGroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionGroupController.class)
public class TransactionGroupControllerTest {

    @MockBean
    private TransactionGroupService transactionGroupService;

    @Autowired
    MockMvc mvc;

    private JacksonTester<TransactionGroup> json;
    private JacksonTester<TransactionRequestDTO> jsonResult;

    TransactionGroup transactionGroup;

    @Before
    public void setup(){
        JacksonTester.initFields(this, new ObjectMapper());
        transactionGroup = new TransactionGroup();
        transactionGroup.addTransaction(
                new TransactionItem(new Card(1L, 4000L), 5000L));
        transactionGroup.addTransaction(
                new TransactionItem(new Card(2L, 4000L), 4000L));
        transactionGroup.addTransaction(
                new TransactionItem(new Card(3L, 4000L), 3400L));
        transactionGroup.addTransaction(
                new TransactionItem(new Card(4L, 4000L), 3300L));
    }

    @Test
    public void testSubmitTransaction() throws Exception {
        TransactionRequestDTO transactionRequestDTO =
                new TransactionRequestDTO(4000L);
        given(transactionGroupService.getTransactions((anyLong()))).willReturn(transactionGroup);
        MockHttpServletResponse response = mvc.perform(
                post("/transactionGroup")
                        .content(jsonResult.write(transactionRequestDTO).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentAsString(), json.write(transactionGroup).getJson());

    }

    @Test
    public void testReport() throws Exception {
        TransactionRequestDTO transactionRequestDTO =
                new TransactionRequestDTO(4000L);
        given(transactionGroupService.report((anyLong()))).willReturn(transactionGroup);
        MockHttpServletResponse response = mvc.perform(
                get("/transactionGroup/10")
                        .content(jsonResult.write(transactionRequestDTO).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentAsString(), json.write(transactionGroup).getJson());
    }
}
