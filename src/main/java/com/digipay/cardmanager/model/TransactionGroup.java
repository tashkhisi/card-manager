package com.digipay.cardmanager.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

@Entity
public final class TransactionGroup {

    @Id
    @GeneratedValue
    public Long id;

    @OneToMany(mappedBy = "transactionGroup")
    @Cascade(CascadeType.ALL)
    private  List<TransactionItem> transactionItems = new LinkedList<>();

    public  TransactionGroup(){
    }

    public void addTransaction(TransactionItem transactionItem){
        transactionItem.setTransactionGroup(this);
        this.transactionItems.add(transactionItem);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<TransactionItem> getTransactionItems() {
        return transactionItems;
    }


}
