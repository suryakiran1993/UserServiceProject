package com.klef.ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse 
{
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Double totalAmount;
}
