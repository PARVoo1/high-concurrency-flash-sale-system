package com.parv.high_concurrency_flash_sale_system.repository;

import com.parv.high_concurrency_flash_sale_system.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional <Inventory> findByProductId(String productId);
}
