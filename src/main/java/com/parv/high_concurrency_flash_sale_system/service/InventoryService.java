package com.parv.high_concurrency_flash_sale_system.service;
import com.parv.high_concurrency_flash_sale_system.event.OrderEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final StringRedisTemplate redisTemplate;

    @Value("classpath:scripts/token_bucket.lua")
    private Resource tokenBucketScriptResource;

    private DefaultRedisScript<Long> tokenScript;

    @PostConstruct
    public void init() {
        tokenScript=new DefaultRedisScript<>();
        tokenScript.setLocation(tokenBucketScriptResource);
        tokenScript.setResultType(Long.class);
    }


    private static final String LUA_SCRIPT =
            "local stock = tonumber(redis.call('GET', KEYS[1])) " +
                    "if stock == nil or stock <= 0 then " +
                    "    return 0 " +
                    "end " +
                    "redis.call('DECR', KEYS[1]) " +
                    "return 1 ";

    public String updateInventory(String productId, String userId) {

        String tokenKey="rate_limit:tokens"+userId;
        String timeKey="rate_limit:time"+userId;
        List<String> keys= Arrays.asList(tokenKey, timeKey);

        String capacity="5";
        String refillRate="1";
        String now=String.valueOf(System.currentTimeMillis());
        String requested="1";

        Long isAllowed=redisTemplate.execute(tokenScript,keys,capacity,refillRate,now,requested);

        if(isAllowed!=null&&isAllowed==0){
            log.warn("🚨 SPAM BLOCKED: User {} ran out of tokens!", userId);
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
