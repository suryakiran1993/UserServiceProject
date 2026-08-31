package com.klef.ms.dto;


import lombok.Data;

@Data


public class ProductResponse 
{
    private Long id;
    private String name;
    private String category;
    private Double price;
    private Integer quantity;
}
