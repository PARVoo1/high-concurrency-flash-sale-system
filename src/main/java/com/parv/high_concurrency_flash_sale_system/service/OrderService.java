package com.parv.high_concurrency_flash_sale_system.service;
import com.parv.high_concurrency_flash_sale_system.entity.Order;
import com.parv.high_concurrency_flash_sale_system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order save(Order order) {
        return orderRepository.save(order);
    }


}
