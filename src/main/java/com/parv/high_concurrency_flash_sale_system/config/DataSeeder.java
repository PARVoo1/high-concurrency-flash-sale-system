package com.parv.high_concurrency_flash_sale_system.config;

import com.parv.high_concurrency_flash_sale_system.entity.Inventory;
import com.parv.high_concurrency_flash_sale_system.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {
    private final InventoryRepository inventoryRepository;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void run(String @NonNull ... args) {
        Inventory item =inventoryRepository.findByProductId("PROD-001")
                .orElseGet(()->{
            Inventory newItem = new Inventory();
            newItem.setProductId("PROD-001");
            newItem.setProductName("Laptop");
            return inventoryRepository.save(newItem);
        });
        item.setStock(10);

        inventoryRepository.save(item);

        redisTemplate.opsForValue().set("inventory:PROD-001:stock", "10");
        log.info("🚀 SYSTEM READY: Seeded PROD-001 with 10 units in both PostgreSQL and Redis.");
    }
}

