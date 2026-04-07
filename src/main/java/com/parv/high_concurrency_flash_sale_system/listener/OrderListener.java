package com.parv.high_concurrency_flash_sale_system.listener;



import com.parv.high_concurrency_flash_sale_system.event.OrderEvent;
import com.parv.high_concurrency_flash_sale_system.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderListener {
    private final ObjectMapper objectMapper;
    private final InventoryRepository inventoryRepository;

    @KafkaListener(topics = "orders-topic", groupId = "flash-sale-group")
    public void listen(String message) {
        try{
            OrderEvent event =objectMapper.readValue(message, OrderEvent.class);
            log.info("Received Order Event: {}", event);

            inventoryRepository.findById(1L).ifPresent(inventory ->{
                inventory.setStock(inventory.getStock()-1);
                inventoryRepository.save(inventory);
            });
        }catch (Exception e){
            log.error("Failed to receive Order Event: {}", message);
        }



    }
}
