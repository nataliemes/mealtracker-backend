package com.tsu.mealtracker.service;

import com.tsu.mealtracker.dto.IngredientDTO;
import com.tsu.mealtracker.dto.IngredientForm;
import com.tsu.mealtracker.model.Ingredient;
import com.tsu.mealtracker.model.User;
import com.tsu.mealtracker.repository.IngredientRepository;
import com.tsu.mealtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepo;
    private final UserRepository userRepo;

    private IngredientDTO toDto(Ingredient ingredient) {
        return new IngredientDTO(ingredient.getId(), ingredient.getName(), ingredient.getSugarPer100g());
    }


    // *** CREATE ******************************************************************************* //

    public IngredientDTO createIngredient(IngredientForm form, String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Ingredient ingredient = new Ingredient();
        ingredient.setName(form.getName());
        ingredient.setSugarPer100g(form.getSugarPer100g());
        ingredient.setUser(user);

        ingredientRepo.save(ingredient);
        return toDto(ingredient);
    }


    // *** GET ******************************************************************************* //

    public List<IngredientDTO> getAllIngredients(UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Ingredient> ingredients;

        if (isAdmin) {
            ingredients = ingredientRepo.findAll();
        } else {
            ingredients = ingredientRepo.findByUserUsername(userDetails.getUsername());
        }

        return ingredients.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }



    public IngredientDTO getIngredientById(Long id, UserDetails userDetails) {
        Ingredient ingredient = ingredientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        // admin can access everything
        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            if (!ingredient.getUser().getUsername().equals(userDetails.getUsername())) {
                throw new AccessDeniedException("You don't have permission to view this ingredient");
            }
        }
        return toDto(ingredient);
    }


    // *** UPDATE ******************************************************************************* //

    public IngredientDTO updateIngredient(Long id, IngredientForm form, UserDetails userDetails) {

        Ingredient ingredient = ingredientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // ingredient can only be updated by admin or the creator
        if (!isAdmin && !ingredient.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You are not allowed to update this ingredient");
        }

        ingredient.setName(form.getName());
        ingredient.setSugarPer100g(form.getSugarPer100g());

        Ingredient saved = ingredientRepo.save(ingredient);
        return toDto(saved);
    }


    // *** DELETE ******************************************************************************* //

    public boolean deleteIngredient(Long id) {
        if (ingredientRepo.existsById(id)) {
            ingredientRepo.deleteById(id);
            return true;
        }
        return false;
    }
}

