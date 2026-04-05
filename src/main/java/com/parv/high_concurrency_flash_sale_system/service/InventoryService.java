package com.parv.high_concurrency_flash_sale_system.service;

import com.parv.high_concurrency_flash_sale_system.entity.Inventory;
import com.parv.high_concurrency_flash_sale_system.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void updateInventory(String productId){
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if(inventory.getStock()>0){
            inventory.setStock(inventory.getStock()-1);
            inventoryRepository.save(inventory);
        }else{
            log.info("Out of Stock{}",inventory.getProductName());
        }
    }

}
