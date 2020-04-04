package com.example.demo.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Employee {

    private Long id;

    private String name;

    private String surname;

    private String job;

    private BigDecimal salary;
}
