package com.chikawa.promotion_service.services;

import com.chikawa.promotion_service.dto.UserEvent;
import com.chikawa.promotion_service.models.User;
import com.chikawa.promotion_service.repositories.PromotionRepository;
import com.chikawa.promotion_service.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class UserListenerEvent {
    PromotionRepository promotionRepository;
    UserRepository userRepository;

    public void createListener(UserEvent event) {
        try{
        log.info("Create Listener, receive event: "+ event.toString());
        User user = new User().builder()
                .id(event.getId())
                .email(event.getEmail())
                .fullName(event.getFullName())
                .lineUserId(event.getLineUserId())
                .monthOfBirth(event.getMonthOfBirth())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .hasReceivedPromotionThisYear(false)
                .build();
        userRepository.save(user);
        System.out.println("Indexed user in promotion: " + event.getId());
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateListener(UserEvent event) {
        try {
            log.info("Update Listener, receive event: "+ event.toString());
            User user = userRepository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("In update listener. User not found!"));

        user.setFullName(event.getFullName());
        user.setMonthOfBirth(event.getMonthOfBirth());
        user.setUpdatedAt(event.getUpdatedAt());
        userRepository.save(user);
        System.out.println("Indexed user in promotion: " + event.getId());

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteListener(UserEvent event) {
        try {
            log.info("Delete Listener, receive event: "+ event.toString());
            User user = userRepository.findById(event.getId())
                    .orElseThrow(() -> new RuntimeException("In delete listener. User not found!"));

            userRepository.deleteById(user.getId());
            System.out.println("Delete user in promotion: " + event.getId());
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "user_promotion_queue")
    public void receiveEvent(UserEvent event) {
        log.info("Received raw event: {}", event); // log toàn bộ object
        switch (event.getAction()){
            case CREATE ->  createListener(event);
            case UPDATE -> updateListener(event);
            case DELETE -> deleteListener(event);
            case null, default -> throw new RuntimeException("In receive data. Error in receive!!");
        }
    }
}
