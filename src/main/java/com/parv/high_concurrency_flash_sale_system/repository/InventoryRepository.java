package com.parv.high_concurrency_flash_sale_system.repository;

import com.parv.high_concurrency_flash_sale_system.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Inventory findByProductId(String productId);
}
