package com.thirdeye.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdeye.code.entity.Tranacation;

import jakarta.transaction.Transaction;

@Repository
public interface TranacationRepository extends JpaRepository<Tranacation, Long> {

    Transaction findtransbynumber(int luckyNumber);
}
