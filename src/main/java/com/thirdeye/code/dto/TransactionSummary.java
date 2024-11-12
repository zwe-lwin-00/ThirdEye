package com.thirdeye.code.dto;

import com.thirdeye.code.entity.Customer;

import lombok.Data;

@Data
public class TransactionSummary {
    private int buynumber;
    private Customer customer;
    private Long totalAmount;
}
