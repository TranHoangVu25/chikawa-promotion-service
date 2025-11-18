package com.chikawa.promotion_service.services;

import com.chikawa.promotion_service.dto.request.PromotionUpdateRequest;
import com.chikawa.promotion_service.models.Promotion;
import org.springframework.stereotype.Service;

@Service
public interface PromotionService {

    Promotion createPromotion(Promotion promotion);

    Promotion updatePromotion(PromotionUpdateRequest request);

    void deletePromotion(Long id);
}
