package com.parv.high_concurrency_flash_sale_system;

import com.parv.high_concurrency_flash_sale_system.entity.Inventory;
import com.parv.high_concurrency_flash_sale_system.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
@Slf4j
public class HighConcurrencyFlashSaleSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(HighConcurrencyFlashSaleSystemApplication.class, args);
	}
	@Bean
	CommandLineRunner seedDatabase(InventoryRepository repository, StringRedisTemplate redisTemplate) {
		return args -> {
			Inventory item = new Inventory();
			item.setId(1L);
			item.setProductId("PROD-001");
			item.setProductName("Gaming Laptop");
			item.setStock(10);

			repository.save(item);

			redisTemplate.opsForValue().set("inventory:PROD-001:stock", "10");

			log.info("Data Seeded: PROD-001 with 10 items.");
		};
	}

}
