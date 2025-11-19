package com.chikawa.promotion_service.services;

import com.chikawa.promotion_service.dto.response.ApiResponse;
import com.chikawa.promotion_service.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    ResponseEntity<List<User>> getUsers();

    ResponseEntity<ApiResponse<String>> deleteUser(Long userId);

}
