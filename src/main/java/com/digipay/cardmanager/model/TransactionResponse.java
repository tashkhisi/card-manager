package com.digipay.cardmanager.model;

import java.util.Date;

public final class TransactionResponse {
    private Date date;
    private TransactionItem transaction;

    public TransactionItem getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionItem transaction) {
        this.transaction = transaction;
    }

    public TransactionResponse(Date date, TransactionItem transaction) {
        this.date = date;
        this.transaction = transaction;
    }

    public Date getDate() {
        return date;
    }
}
