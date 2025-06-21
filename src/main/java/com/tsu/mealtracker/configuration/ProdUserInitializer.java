package com.tsu.mealtracker.configuration;

import com.tsu.mealtracker.model.Authority;
import com.tsu.mealtracker.model.User;
import com.tsu.mealtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Profile("prod")
@RequiredArgsConstructor
@Slf4j
public class ProdUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("pass"));
            admin.setEnabled(true);

            Authority authUser = new Authority();
            authUser.setAuthority("ROLE_ADMIN");
            authUser.setUser(admin);

            Set<Authority> authorities = new HashSet<>();
            authorities.add(authUser);
            admin.setAuthorities(authorities);

            userRepository.save(admin);

            log.info("Created an admin.");
        }
        else {
            log.info("Admin already exists.");
        }
    }
}

