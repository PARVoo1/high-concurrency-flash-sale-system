package com.parv.high_concurrency_flash_sale_system.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    Long id;
    private String productId;
    private String productName;
    private int stock;
}

