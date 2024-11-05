package com.thirdeye.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdeye.code.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
