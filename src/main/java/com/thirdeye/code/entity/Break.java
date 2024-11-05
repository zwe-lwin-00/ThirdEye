package com.thirdeye.code.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Break {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long breakid;
    private int breakamount;
    private LocalDateTime updateddate;
}
