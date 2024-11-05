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
public class Tranacation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tranacationid;
    private LocalDateTime transcationdate;
    @ManyToOne
    private Number number; // Reference to Number entity

    @ManyToOne
    private Customer customer; // Reference to Customer entity
}
