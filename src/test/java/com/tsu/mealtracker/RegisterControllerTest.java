package com.tsu.mealtracker;

import com.tsu.mealtracker.model.User;
import com.tsu.mealtracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class RegisterControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testRegisterUser() {

        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "john");
        registerRequest.put("password", "johnsVerySecurePassword123#");


        ResponseEntity<Void> registerResponse = restTemplate.postForEntity(
                "/auth/register",
                registerRequest,
                Void.class
        );

        // registration succeeded
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // username saved in database
        User savedUser = userRepository.findById("john").orElseThrow();
        assertThat(savedUser.isEnabled()).isTrue();

        // authenticate with Basic Auth on a secured endpoint (/profile)
        TestRestTemplate authRestTemplate = restTemplate.withBasicAuth("john", "johnsVerySecurePassword123#");
        ResponseEntity<String> securedResponse = authRestTemplate.getForEntity("/profile", String.class);

        assertThat(securedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

