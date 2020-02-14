package com.example.ws.rest.controller;

import com.example.ws.rest.config.Destination;
import com.example.ws.rest.domain.User;
import com.example.ws.rest.domain.UserResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @MessageMapping("/app/user")
    @SendToUser(Destination.FINAL)
    public UserResponse getUser(User user) {
        return new UserResponse(user.getName());
    }
}
