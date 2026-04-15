package com.parv.high_concurrency_flash_sale_system.repository;

import com.parv.high_concurrency_flash_sale_system.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
