package com.thirdeye.code.dto;

import java.util.List;

import lombok.Data;

@Data
public class PermutationData {
    private List<String> number;
    private List<String> remain;
    private List<String> amt;
    private List<String> my;
    private List<String> buy;
    private String customerid;
}
