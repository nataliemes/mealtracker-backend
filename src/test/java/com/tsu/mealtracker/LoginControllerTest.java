package com.tsu.mealtracker;

import com.tsu.mealtracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class LoginControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testLoginSuccess() {

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "anna");
        loginRequest.put("password", "anna");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/login",
                loginRequest,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testLoginFailWrongPassword() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "anna");
        loginRequest.put("password", "thisIsWrong");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/login",
                loginRequest,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testLoginFailUserNotFound() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "nobody");
        loginRequest.put("password", "nothing");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/login",
                loginRequest,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}

