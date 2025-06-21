package com.tsu.mealtracker.service;

import com.tsu.mealtracker.model.Authority;
import com.tsu.mealtracker.model.User;
import com.tsu.mealtracker.repository.AuthorityRepository;
import com.tsu.mealtracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean existsUsername(String username) {
        boolean exists = userRepository.existsById(username);
        log.debug("Checking existence of username '{}': {}", username, exists);
        return exists;
    }

    @Transactional
    public void register(String username, String rawPassword) {
        log.info("Registering new user with username '{}'", username);

        String encodedPassword = passwordEncoder.encode(rawPassword);

        User newUser = new User(username, encodedPassword, true, null, null, null);
        userRepository.save(newUser);

        Authority authority = new Authority(null, newUser, "ROLE_USER");
        authorityRepository.save(authority);

        newUser.setAuthorities(Set.of(authority));

        log.info("User '{}' registered successfully with role ROLE_USER", username);
    }
}
