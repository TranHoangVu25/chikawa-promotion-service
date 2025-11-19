package com.chikawa.promotion_service.services;

import com.chikawa.promotion_service.dto.request.PromotionUpdateRequest;
import com.chikawa.promotion_service.dto.response.ApiResponse;
import com.chikawa.promotion_service.exception.ErrorCode;
import com.chikawa.promotion_service.models.Promotion;
import com.chikawa.promotion_service.models.User;
import com.chikawa.promotion_service.repositories.PromotionRepository;
import com.chikawa.promotion_service.repositories.UserRepository;
import com.chikawa.promotion_service.utils.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Coupon;
import com.stripe.model.PromotionCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class PromotionServiceImpl implements PromotionService {
    PromotionRepository promotionRepository;
    UserRepository userRepository;
    Stripe stripe;

    @Override
    public ResponseEntity<ApiResponse<List<Promotion>>> getAllPromotion() {
        List<Promotion> promotions = promotionRepository.findAll();
        if(promotions.isEmpty()){
            return ResponseEntity.ok()
                    .body(
                            ApiResponse.<List<Promotion>>builder()
                                    .message("List promotion is empty")
                                    .build()
                    );
        }
        return ResponseEntity.ok()
                .body(
                        ApiResponse.<List<Promotion>>builder()
                                .result(promotions)
                                .build()
                );
    }

    @Override
    public ResponseEntity<ApiResponse<Promotion>> getPromotionById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse<Promotion>> createPromotion(Promotion p, Long userId) {

        //Kiểm tra code đã tồn tại
        if (promotionRepository.existsByCode(p.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Promotion>builder()
                            .message(ErrorCode.PROMOTION_EXISTED.getMessage())
                            .build());
        }

        //Kiểm tra user tồn tại
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Promotion>builder()
                            .message(ErrorCode.USER_NOT_EXISTED.getMessage())
                            .build());
        }

        User user = userRepository.findById(userId).get();

        try {
            //Tạo coupon trên Stripe
            Coupon coupon = stripe.createStripeCoupon(p.getValue());

            //Tạo promotion code trên Stripe
            PromotionCode stripePromotion = stripe.createStripePromotionCode(coupon.getId(), p.getCode());

            //Lưu vào DB
            Promotion promotion = Promotion.builder()
                    .value(p.getValue())
                    .description(p.getDescription())
                    .code(p.getCode())
                    .isUsed(false)
                    .createdAt(LocalDateTime.now())
                    .expireDate(p.getExpireDate())
                    .stripeCouponId(coupon.getId())
                    .stripePromotionId(stripePromotion.getId())
                    .user(user)
                    .build();

            promotionRepository.save(promotion);

            return ResponseEntity.ok().body(
                    ApiResponse.<Promotion>builder()
                            .message("Create promotion successfully")
                            .result(promotion)
                            .build()
            );

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Promotion>builder()
                            .message("Stripe error: " + e.getMessage())
                            .build());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Promotion>> updatePromotion(PromotionUpdateRequest request, Long userId) {

        //Kiểm tra tồn tại promotion
        Promotion promotion = promotionRepository.findByCode(request.getCode())
                .orElseThrow(() -> new RuntimeException("Promotion code not existed"));

        //Kiểm tra tồn tại user
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Promotion>builder()
                            .message(ErrorCode.USER_NOT_EXISTED.getMessage())
                            .build());
        }

        try {
            //Cập nhật coupon trên Stripe
            if (promotion.getStripeCouponId() != null) {
                Map<String, Object> params = new HashMap<>();
                params.put("metadata", Map.of(
                        "description", request.getDescription()
                ));
                // Stripe không cho update percent_off trực tiếp, nếu muốn thay đổi value phải tạo coupon mới
                Coupon stripeCoupon = Coupon.retrieve(promotion.getStripeCouponId());
                stripeCoupon.update(params);
            }

            //Update promotion trong DB
            promotion.setDescription(request.getDescription());
            promotion.setUpdatedAt(LocalDateTime.now());
            promotion.setExpireDate(request.getExpireDate());

            Promotion updatedPromotion = promotionRepository.save(promotion);

            return ResponseEntity.ok(ApiResponse.<Promotion>builder()
                    .message("Update promotion successfully")
                    .result(updatedPromotion)
                    .build());

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Promotion>builder()
                            .message("Stripe error: " + e.getMessage())
                            .build());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<String>> deletePromotion(Long promotionId) {
        // Lấy promotion từ DB
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElse(null);

        if (promotion == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<String>builder()
                            .message(ErrorCode.PROMOTION_NOT_EXISTED.getMessage())
                            .build());
        }

        try {
            // Disable PromotionCode trên Stripe
            if (promotion.getStripePromotionId() != null) {
                PromotionCode stripePromotion = PromotionCode.retrieve(promotion.getStripePromotionId());
                Map<String, Object> params = new HashMap<>();
                params.put("active", false); // disable code
                stripePromotion.update(params);
            }

            // Coupon: không có trường active, dùng metadata để đánh dấu inactive
            if (promotion.getStripeCouponId() != null) {
                Coupon stripeCoupon = Coupon.retrieve(promotion.getStripeCouponId());
                Map<String, Object> couponParams = new HashMap<>();
                couponParams.put("metadata", Map.of("status", "inactive"));
                stripeCoupon.update(couponParams);
            }

            // Xóa promotion trong DB
            promotionRepository.deleteById(promotionId);

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .message("Promotion disabled on Stripe and deleted from DB successfully")
                    .build());

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<String>builder()
                            .message("Stripe error: " + e.getMessage())
                            .build());
        }
    }


}
