package com.digipay.cardmanager.repository;

import com.digipay.cardmanager.model.TransactionItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface TransactionItemRepository extends CrudRepository<TransactionItem, Long> {
}
