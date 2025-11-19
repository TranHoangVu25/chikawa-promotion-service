package com.chikawa.promotion_service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotions", indexes = {
        @Index(name = "idx_promotions_code", columnList = "code", unique = true),
        @Index(name = "idx_promotions_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // tránh lỗi Jackson không biết cách chuyển proxy Hibernate thành JSON
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer value;
    private String description;
    private String code;
    private Boolean isUsed = false;

    private String stripeCouponId;
    private String stripePromotionId;
    private boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expireDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
