package com.tsu.mealtracker;

import com.tsu.mealtracker.model.Ingredient;
import com.tsu.mealtracker.repository.IngredientRepository;
import com.tsu.mealtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IngredientControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IngredientRepository ingredientRepository;


    @Test
    void testCreateIngredientSuccess() {

        Map<String, Object> request = new HashMap<>();
        request.put("name", "Salt");
        request.put("sugarPer100g", 0);

        // Authenticate using basic auth
        TestRestTemplate authRestTemplate = restTemplate.withBasicAuth("anna", "anna");

        ResponseEntity<String> response = authRestTemplate.postForEntity(
                "/ingredients",
                request,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // verify ingredient was saved in database
        List<Ingredient> allIngredients = ingredientRepository.findAll();
        assertThat(allIngredients).hasSize(1);
        assertThat(allIngredients.getFirst().getName()).isEqualTo("Salt");
        assertThat(allIngredients.getFirst().getUser().getUsername()).isEqualTo("anna");
    }
}

