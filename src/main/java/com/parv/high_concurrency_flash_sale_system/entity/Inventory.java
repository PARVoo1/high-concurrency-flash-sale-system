package com.parv.high_concurrency_flash_sale_system.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Inventory {
    @Id
    Long id;
    private String productId;
    private String productName;
    private int stock;
}

