package com.digipay.cardmanager.model;
;
import com.digipay.cardmanager.core.util.MultiValueTreeElement;
import org.hibernate.secure.spi.IntegrationException;

import javax.persistence.*;

@Entity()
@Table(indexes ={@Index(name = "amount_index", columnList = "transferable_amount", unique = false)})
public final class Card implements MultiValueTreeElement<Long, Long> {

    @SequenceGenerator(name = "mySeqGen", sequenceName = "myDbSeq",
            initialValue = 10270, allocationSize = 3)
    @GeneratedValue(generator = "mySeqGen")
    @Id
    private Long id;

    @Column(name="transferable_amount")
    private long transferableAmount;

    protected Card(){

    }

    @Override
    public Long getId() {
        return id ;
    }

    public Card(final Long cardId, final Long transferableAmount) {
        this.id = cardId;
        this.transferableAmount = transferableAmount;
    }

    public  Card(Long transferableAmount){
        this.transferableAmount = transferableAmount;
    }

    public void setId(Long cardId) {
        this.id = cardId;
    }

    public Long getTransferableAmount() {
        return transferableAmount;
    }

    @Override
    public String toString() {
        return "Card{" +
                " transferableAmount=" + transferableAmount +
                '}';
    }

    @Override
    public Long getPriority() {
        return transferableAmount;
    }
}
