package com.digipay.cardmanager.controller;


import com.digipay.cardmanager.data.TransactionRequestDTO;
import com.digipay.cardmanager.model.TransactionGroup;
import com.digipay.cardmanager.service.TransactionGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionGroupController {

    @Autowired
    private TransactionGroupService transactionGroupService;

    @Autowired
    public TransactionGroupController(TransactionGroupService transactionGroupService){
        this.transactionGroupService = transactionGroupService;
    }

    @PostMapping("/transactionGroup")
    public ResponseEntity<TransactionGroup> submitTransaction(@RequestBody TransactionRequestDTO transactionRequestDTO){
        TransactionGroup  transactionGroup =
                transactionGroupService.getTransactions(transactionRequestDTO.getTotalAmount());
        return ResponseEntity.ok(transactionGroup);
    }

    @GetMapping(value="/transactionGroup/{groupId}")
    public TransactionGroup report(@PathVariable Long groupId){
        return transactionGroupService.report(groupId);
    }

    @GetMapping(value="/transactionGroup")
    public List<TransactionGroup> reportAll(){
        return transactionGroupService.report();
    }

}