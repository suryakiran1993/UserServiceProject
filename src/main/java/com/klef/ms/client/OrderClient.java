package com.klef.ms.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.klef.ms.dto.OrderResponse;

@FeignClient(name = "OrderService")
public interface OrderClient 
{
	@GetMapping("/order/displaybyuser")
	List<OrderResponse> displayordersbyuserid(@RequestParam Long userid);
	
}
