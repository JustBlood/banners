package ru.just.banners.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.just.banners.dto.UserTokenDto;
import ru.just.banners.service.UserService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserTokenDto> createUser() {
        return new ResponseEntity<>(userService.createUser(), HttpStatus.CREATED);
    }
}
