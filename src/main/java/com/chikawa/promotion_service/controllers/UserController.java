package com.chikawa.promotion_service.controllers;

import com.chikawa.promotion_service.dto.response.ApiResponse;
import com.chikawa.promotion_service.models.User;
import com.chikawa.promotion_service.repositories.UserRepository;
import com.chikawa.promotion_service.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable Long userId
    ) {
        return userService.deleteUser(userId);
    }
}
