package com.parv.high_concurrency_flash_sale_system.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String productId;
    private String productName;
    private int stock;
}

