package com.parv.high_concurrency_flash_sale_system.controller;
import com.parv.high_concurrency_flash_sale_system.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/{productId}")
    public ResponseEntity<String> purchase(@PathVariable String productId, Principal principal) {

        String userId = principal.getName();
        String response = inventoryService.updateInventory(productId, userId);
        if(response.equals("RATE_LIMITED")){
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Error: Too many requests. Please slow down.");
        }
        else if (response.equals("OUT_OF_STOCK")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Item is out of stock.");
        }
            return ResponseEntity.ok().build();


    }
}
