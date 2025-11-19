package com.chikawa.promotion_service.utils;

import com.stripe.exception.StripeException;
import com.stripe.model.Coupon;
import com.stripe.model.PromotionCode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Stripe {
    public Coupon createStripeCoupon(Integer percent) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("percent_off", percent);
        params.put("duration", "once"); // dùng 1 lần

        return Coupon.create(params);
    }

    public PromotionCode createStripePromotionCode(String couponId, String code) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("coupon", couponId);
        params.put("code", code);

        return PromotionCode.create(params);
    }

}
