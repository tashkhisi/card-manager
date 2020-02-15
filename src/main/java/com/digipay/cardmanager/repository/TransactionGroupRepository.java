package com.digipay.cardmanager.repository;

import com.digipay.cardmanager.model.TransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface TransactionGroupRepository extends JpaRepository<TransactionGroup, Long> {
}
