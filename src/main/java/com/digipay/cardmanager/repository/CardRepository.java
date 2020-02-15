package com.digipay.cardmanager.repository;

import com.digipay.cardmanager.model.Card;
import com.digipay.cardmanager.service.TransactionGroupService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

/**
 *  this service provide us with last cards and current transferable amount
 *  of  that card, we should  trust this service with last transferable amount of each card.
 *  the method getAllCards of this service  only called when the system restart or when the
 *  restart constraint job execute at 00:00. if we don't have such service  we could be left
 *  behind unreliable situation because some transfer request have sent to the bank and we don't know
 *  what is their status(for example because of unwanted restart of service or because we have sent some
 *  request to the bank before the {@link TransactionGroupService#scheduleForResetConstraints()} execution and we receive the response
 *  after execution of that job
 */
@Service
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query(nativeQuery = true, value = "select * from card where card.TRANSFERABLE_AMOUNT > ? and " +
            "card.transferable_amount > 10000 limit ?")
    Card ceiling(long mount, long count);

//    @Query(value = "select new com.digipay.cardmanager.model.card(id) idcmax(transferable_amount) from card a group by id")
//    Card max();

    @Query(nativeQuery = true, value = "select sum(transferable_amount)  from card ")
    long sum();

    Card findFirstByOrderByTransferableAmountDesc();
}
