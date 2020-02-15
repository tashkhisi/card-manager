package com.digipay.cardmanager.service;

import com.digipay.cardmanager.model.TransactionResponse;

public interface ResponseHandler {
    void onFailed(TransactionResponse response);
    void onSuccess(TransactionResponse response);
    void onInterrupt(TransactionResponse response);
}
