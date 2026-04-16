package com.parv.high_concurrency_flash_sale_system.repository;

import com.parv.high_concurrency_flash_sale_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
