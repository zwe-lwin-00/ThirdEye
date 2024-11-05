package com.thirdeye.code.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Number {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numberid;
    private int number;
    private int ownamount;
    private int buyamount;
}
