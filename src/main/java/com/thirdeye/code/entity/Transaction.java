package com.thirdeye.code.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionid;
    private LocalDateTime transactiondate;
    @ManyToOne
    private Number number;

    @ManyToOne
    private Customer customer;

    private int amount;
    private int buynumber;
}
