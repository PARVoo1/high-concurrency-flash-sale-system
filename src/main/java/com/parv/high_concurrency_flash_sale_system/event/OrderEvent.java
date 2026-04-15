package com.parv.high_concurrency_flash_sale_system.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private String ticketId;
    private String userId;
    private String productId;
    private String status;
    private long timestamp;
}
