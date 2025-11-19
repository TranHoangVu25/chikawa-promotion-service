package com.chikawa.promotion_service.dto;

import com.chikawa.promotion_service.enums.Action;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEvent {
    private Long id;
    private String email;
    private String fullName;
    private String lineUserId;
    private Integer monthOfBirth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Action action;
}
