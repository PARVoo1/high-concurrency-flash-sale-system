package com.parv.high_concurrency_flash_sale_system.controller;

import com.parv.high_concurrency_flash_sale_system.entity.Inventory;
import com.parv.high_concurrency_flash_sale_system.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/{productId}")
    public ResponseEntity<Inventory> purchase(@PathVariable String productId) {
        inventoryService.updateInventory(productId);
        return ResponseEntity.ok().build();
    }
}
