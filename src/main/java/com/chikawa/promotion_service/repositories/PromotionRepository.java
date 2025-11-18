package com.chikawa.promotion_service.repositories;

import com.chikawa.promotion_service.models.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion,Long> {
}
