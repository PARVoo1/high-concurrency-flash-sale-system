package com.parv.high_concurrency_flash_sale_system.listener;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.parv.high_concurrency_flash_sale_system.entity.Order;
import com.parv.high_concurrency_flash_sale_system.event.OrderEvent;
import com.parv.high_concurrency_flash_sale_system.repository.InventoryRepository;
import com.parv.high_concurrency_flash_sale_system.repository.OrderRepository;
import com.parv.high_concurrency_flash_sale_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class OrderListener {
    private final ObjectMapper objectMapper =new ObjectMapper();
    private final InventoryRepository inventoryRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "orders-topic", groupId = "flash-sale-group")
    public void listen(String message) {
        try{
            OrderEvent event =objectMapper.readValue(message, OrderEvent.class);
            log.info("Received Order Event: {}", event);

            if (orderRepository.existsById(event.getTicketId())) {
                log.warn("Duplicate ticket detected and ignored: {}", event.getTicketId());
                return;
            }
            Order order = Order.builder()
                    .id(event.getTicketId())
                    .productId(event.getProductId())
                    .userId(event.getUserId())
                    .status(event.getStatus())
                    .orderTimeStamp(event.getTimestamp())
                    .build();
            orderService.save(order);

            String productId = event.getProductId();

            inventoryRepository.findByProductId(productId).ifPresent(inventory ->{
                inventory.setStock(inventory.getStock()-1);
                inventoryRepository.save(inventory);
            });
        }catch (Exception e){
            log.error("Failed to receive Order Event: {}", message);
        }



    }
}
