package com.thirdeye.code.dto;

import com.thirdeye.code.entity.Customer;

import lombok.Data;

@Data
public class TranacationSummary {
    private int buynumber;
    private Customer customer;
    private Long totalAmount;
}
