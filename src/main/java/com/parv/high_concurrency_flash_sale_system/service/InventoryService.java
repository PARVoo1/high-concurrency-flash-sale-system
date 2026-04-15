package com.parv.high_concurrency_flash_sale_system.service;
import com.parv.high_concurrency_flash_sale_system.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final StringRedisTemplate redisTemplate;
    private static final String LUA_SCRIPT =
            "local stock = tonumber(redis.call('GET', KEYS[1])) " +
                    "if stock == nil or stock <= 0 then " +
                    "    return 0 " +
                    "end " +
                    "redis.call('DECR', KEYS[1]) " +
                    "return 1 ";

    public String updateInventory(String productId, String userId) {

        String rateLimitKey="rate_limit"+userId;

        Long requestCount=redisTemplate.opsForValue().increment(rateLimitKey,1);
        if(requestCount!=null&&requestCount==1){
            redisTemplate.expire(rateLimitKey, Duration.ofSeconds(1));
        }
        if(requestCount!=null&&requestCount>5){
            log.warn("🚨 SPAM BLOCKED: User {} exceeded 5 requests per second", userId);
            return "RATE_LIMITED";
        }

        String redisKey ="inventory:"+productId+":stock";

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(LUA_SCRIPT);
        script.setResultType(Long.class);

        Long result=redisTemplate.execute(script, Collections.singletonList(redisKey));

        if(result!=null && result==1){
            log.info("Success: Order Placed for{}",productId);

            String tickerId=java.util.UUID.randomUUID().toString();

            OrderEvent event = new OrderEvent(tickerId,userId,productId, "CONFIRMED", System.currentTimeMillis());

            kafkaTemplate.send("orders-topic", productId, event);

            log.info("ASYNC: Ticket sent to Kafka queue for {}", productId);
            return "TICKET_SENT";

        }
        else{
            log.info("Out of Stock{}",productId);
            return "OUT_OF_STOCK";
        }
    }

}
