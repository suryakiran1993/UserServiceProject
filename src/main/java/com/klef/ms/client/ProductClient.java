package com.klef.ms.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.klef.ms.dto.ProductResponse;

@FeignClient(name = "ProductService")
public interface ProductClient 
{
	@GetMapping("product/displayall")
	List<ProductResponse> getAllProducts();

}