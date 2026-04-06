package com.parv.high_concurrency_flash_sale_system.service;
import com.parv.high_concurrency_flash_sale_system.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StringRedisTemplate redisTemplate;
    private static final String LUA_SCRIPT =
            "local stock = tonumber(redis.call('GET', KEYS[1])) " +
                    "if stock == nil or stock <= 0 then " +
                    "    return 0 " +
                    "end " +
                    "redis.call('DECR', KEYS[1]) " +
                    "return 1 ";

    public void updateInventory(String productId){

        String redisKey ="inventory:"+productId+":stock";

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(LUA_SCRIPT);
        script.setResultType(Long.class);

        Long result=redisTemplate.execute(script, Collections.singletonList(redisKey));

        if(result!=null && result==1){
            log.info("Success: Order Placed for{}",productId);

            inventoryRepository.findById(1L).ifPresent(inventory -> {
                inventory.setStock(inventory.getStock() - 1);
                inventoryRepository.save(inventory);
            });
        }
        else{
            log.info("Out of Stock{}",productId);
        }
    }

}
