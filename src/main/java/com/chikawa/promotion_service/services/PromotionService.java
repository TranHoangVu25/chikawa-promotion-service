package com.chikawa.promotion_service.services;

import com.chikawa.promotion_service.dto.request.PromotionUpdateRequest;
import com.chikawa.promotion_service.dto.response.ApiResponse;
import com.chikawa.promotion_service.models.Promotion;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PromotionService {

    ResponseEntity<ApiResponse<List<Promotion>>> getAllPromotion();

    ResponseEntity<ApiResponse<Promotion>> getPromotionById(Long id);

    ResponseEntity<ApiResponse<Promotion>> createPromotion(Promotion promotion, Long userId);

    ResponseEntity<ApiResponse<Promotion>> updatePromotion(PromotionUpdateRequest request,Long userId);

    ResponseEntity<ApiResponse<String>> deletePromotion(Long promotionId);
}
