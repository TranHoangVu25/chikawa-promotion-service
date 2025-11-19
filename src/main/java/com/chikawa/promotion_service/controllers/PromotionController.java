package com.chikawa.promotion_service.controllers;

import com.chikawa.promotion_service.dto.request.FormCreatePromotionRequest;
import com.chikawa.promotion_service.dto.request.PromotionUpdateRequest;
import com.chikawa.promotion_service.dto.response.ApiResponse;
import com.chikawa.promotion_service.models.Promotion;
import com.chikawa.promotion_service.services.PromotionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/promotion")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PromotionController {
    PromotionService promotionService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<Promotion>>> getAllPromotion(){
        return promotionService.getAllPromotion();
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<Promotion>> createPromotion(
            @RequestBody FormCreatePromotionRequest request

    ){
        return promotionService.createPromotion(request.getPromotion(), request.getUserId());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<Promotion>> updatePromotion(
            @RequestBody @Valid PromotionUpdateRequest request,
            @PathVariable Long userId
            ){
        return promotionService.updatePromotion(request,userId);
    }

    @DeleteMapping("/{promotionId}")
    public ResponseEntity<ApiResponse<String>> deletePromotion(
            @PathVariable Long promotionId
    ){
        return promotionService.deletePromotion(promotionId);
    }
}
