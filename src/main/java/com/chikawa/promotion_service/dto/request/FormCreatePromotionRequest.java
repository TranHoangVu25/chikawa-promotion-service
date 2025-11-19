package com.chikawa.promotion_service.dto.request;

import com.chikawa.promotion_service.models.Promotion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//dùng trong controller, create promotion
public class FormCreatePromotionRequest {
    Promotion promotion;
    Long userId;
}
