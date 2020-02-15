package com.digipay.cardmanager.data;

public class TransactionRequestDTO {
    Long totalAmount;

    public TransactionRequestDTO(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public TransactionRequestDTO() {
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
