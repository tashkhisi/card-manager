package com.digipay.cardmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public final class TransactionItem {

    @Id
    @GeneratedValue
    private Long id;
    private TransactionStatus status;
    private Long amount;
    @OneToOne
//    @Transient
    private Card card;

    protected TransactionItem(){

    }

    public TransactionItem(Card card, Long amount){
        this.card = card;
        this.status = TransactionStatus.PENDING;
        this.amount = amount;

    }

    @ManyToOne
    @JoinColumn(name = "transactionGroup")
    @JsonIgnore
    private TransactionGroup transactionGroup;

    public Long getId() {
        return id;
    }

    public Card getCard() {
        return card;
    }

    @Override
    public String toString() {
        return "TransferTransaction{" +
                "amount=" + amount +
                '}';
    }
    public Long getAmount() {
        return amount;
    }

    public TransactionGroup getTransactionGroup() {
        return transactionGroup;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public void setTransactionGroup(TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }

    public enum TransactionStatus {
        FAILED,
        SUCCEED,
        PENDING,
        CONTRADICTION,
    }
}
