package ru.just.banners.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.just.banners.dto.UserTokenDto;
import ru.just.banners.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserTokenDto createUser() {
        return new UserTokenDto(userRepository.createUser());
    }
}
