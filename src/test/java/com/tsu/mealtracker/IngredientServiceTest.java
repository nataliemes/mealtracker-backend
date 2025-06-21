package com.tsu.mealtracker;


import com.tsu.mealtracker.dto.IngredientDTO;
import com.tsu.mealtracker.dto.IngredientForm;
import com.tsu.mealtracker.model.Ingredient;
import com.tsu.mealtracker.model.User;
import com.tsu.mealtracker.repository.IngredientRepository;
import com.tsu.mealtracker.repository.UserRepository;
import com.tsu.mealtracker.service.IngredientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceTest {

    @InjectMocks
    private IngredientService ingredientService;

    @Mock
    private IngredientRepository ingredientRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private UserDetails userDetails;


    @Test
    void createIngredientSuccess() {

        IngredientForm form = new IngredientForm("Salt", 0L);

        User user = new User();
        user.setUsername("john");

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));

        Ingredient savedIngredient = new Ingredient();
        savedIngredient.setId(1L);
        savedIngredient.setName("Salt");
        savedIngredient.setUser(user);

        when(ingredientRepo.save(any(Ingredient.class))).thenReturn(savedIngredient);

        IngredientDTO dto = ingredientService.createIngredient(form, "john");

        assertNotNull(dto);
        assertEquals("Salt", dto.getName());
        assertEquals(1L, dto.getId());

        verify(userRepo).findByUsername("john");
        verify(ingredientRepo).save(any(Ingredient.class));
    }


    @Test
    void deleteIngredientNotFound() {
        when(userDetails.getUsername()).thenReturn("john");
        when(ingredientRepo.findById(1L)).thenReturn(Optional.empty());

        boolean result = ingredientService.deleteIngredient(1L, userDetails);

        assertFalse(result);
        verify(ingredientRepo, never()).deleteById(any());
    }

    @Test
    void deleteIngredientWrongUser() {

        User user = new User();
        user.setUsername("alice");

        Ingredient ingredient = new Ingredient();
        ingredient.setId(2L);
        ingredient.setUser(user);

        when(userDetails.getUsername()).thenReturn("john");
        when(ingredientRepo.findById(2L)).thenReturn(Optional.of(ingredient));

        assertThrows(AccessDeniedException.class, () ->
                ingredientService.deleteIngredient(2L, userDetails)
        );

        verify(ingredientRepo, never()).deleteById(any());
    }

    @Test
    void deleteIngredientSuccess() {

        User user = new User();
        user.setUsername("john");

        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Salt");
        ingredient.setUser(user);

        when(userDetails.getUsername()).thenReturn("john");
        when(ingredientRepo.findById(1L)).thenReturn(Optional.of(ingredient));

        boolean result = ingredientService.deleteIngredient(1L, userDetails);

        assertTrue(result);
        verify(ingredientRepo).findById(1L);
        verify(ingredientRepo).deleteById(1L);
    }

}
