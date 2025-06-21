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
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private void saveNewUser(String username, String password, boolean isAdmin){

        User user = new User();

        user.setUsername(username);
        user.setPassword(password);
        user.setEnabled(true);

        Authority authUser = new Authority();
        authUser.setAuthority(isAdmin ? "ROLE_ADMIN" : "ROLE_USER");
        authUser.setUser(user);

        Set<Authority> authorities = new HashSet<>();
        authorities.add(authUser);
        user.setAuthorities(authorities);

        userRepository.save(user);

        log.info("Created user '{}' with role '{}'", username, isAdmin ? "ADMIN" : "USER");
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("No users found in database, initializing default users...");
            saveNewUser("admin", passwordEncoder.encode("adminpass"), true);
            saveNewUser("anna", passwordEncoder.encode("anna"), false);
            saveNewUser("amelia", passwordEncoder.encode("amelia"), false);
            saveNewUser("luca", passwordEncoder.encode("luca"), false);
            log.info("User initialization complete.");
        }
        else {
            log.info("Users already exist in the database. Skipping initialization.");
        }
    }
}
