package com.chikawa.promotion_service.services;

import com.chikawa.promotion_service.dto.response.ApiResponse;
import com.chikawa.promotion_service.exception.ErrorCode;
import com.chikawa.promotion_service.models.User;
import com.chikawa.promotion_service.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public ResponseEntity<List<User>> getUsers() {
        if (userRepository.findAll().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userRepository.findAll());
    }

    @Override
    public ResponseEntity<ApiResponse<String>> deleteUser(Long userId) {
        if(!userRepository.existsById(userId)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            ApiResponse.<String>builder()
                                    .message(ErrorCode.USER_NOT_EXISTED.getMessage())
                                    .build()
                    );
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok()
                .body(
                        ApiResponse.<String>builder()
                                .message("Delete user successfully")
                                .build()
                );
    }
}
