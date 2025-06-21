package com.tsu.mealtracker.service;

import com.tsu.mealtracker.dto.IngredientDTO;
import com.tsu.mealtracker.dto.IngredientForm;
import com.tsu.mealtracker.model.Ingredient;
import com.tsu.mealtracker.model.User;
import com.tsu.mealtracker.repository.IngredientRepository;
import com.tsu.mealtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
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
        log.info("Creating ingredient '{}' for user '{}'", form.getName(), username);

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User '{}' not found while creating ingredient", username);
                    return new UsernameNotFoundException("User not found");
                });

        Ingredient ingredient = new Ingredient();
        ingredient.setName(form.getName());
        ingredient.setSugarPer100g(form.getSugarPer100g());
        ingredient.setUser(user);

        Ingredient saved = ingredientRepo.save(ingredient);

        log.info("Ingredient '{}' created successfully for user '{}', id={}", saved.getName(), saved, saved.getId());
        return toDto(saved);
    }


    // *** GET ******************************************************************************* //

    public List<IngredientDTO> getAllIngredients(UserDetails userDetails) {
        log.info("User '{}' requested all ingredients", userDetails.getUsername());

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Ingredient> ingredients;

        if (isAdmin) {
            ingredients = ingredientRepo.findAll();
            log.info("User '{}' is admin: returning all ingredients (count: {})",
                    userDetails.getUsername(), ingredients.size());
        } else {
            ingredients = ingredientRepo.findByUserUsername(userDetails.getUsername());
            log.info("User '{}' is not admin: returning own ingredients (count: {})",
                    userDetails.getUsername(), ingredients.size());
        }

        return ingredients.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    public IngredientDTO getIngredientById(Long id, UserDetails userDetails) {
        log.info("User '{}' requested ingredient with ID: {}", userDetails.getUsername(), id);

        Ingredient ingredient = ingredientRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ingredient with ID {} not found for user {}", id, userDetails.getUsername());
                    return new RuntimeException("Ingredient not found");
                });

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !ingredient.getUser().getUsername().equals(userDetails.getUsername())) {
            log.warn("User '{}' unauthorized to view ingredient {}", userDetails.getUsername(), id);
            throw new AccessDeniedException("You don't have permission to view this ingredient");
        }

        log.info("User '{}' granted access to ingredient {}", userDetails.getUsername(), id);
        return toDto(ingredient);
    }


    // *** UPDATE ******************************************************************************* //

    public IngredientDTO updateIngredient(Long id, IngredientForm form, UserDetails userDetails) {
        log.info("User '{}' requested to update ingredient with ID: {}", userDetails.getUsername(), id);

        Ingredient ingredient = ingredientRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ingredient with ID {} not found for update by user {}", id, userDetails.getUsername());
                    return new RuntimeException("Ingredient not found");
                });

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !ingredient.getUser().getUsername().equals(userDetails.getUsername())) {
            log.warn("User '{}' is not authorized to update ingredient {}", userDetails.getUsername(), id);
            throw new AccessDeniedException("You are not allowed to update this ingredient");
        }

        ingredient.setName(form.getName());
        ingredient.setSugarPer100g(form.getSugarPer100g());

        Ingredient saved = ingredientRepo.save(ingredient);
        log.info("Ingredient with ID {} updated successfully by user '{}'", id, userDetails.getUsername());

        return toDto(saved);
    }


    // *** DELETE ******************************************************************************* //

    public boolean deleteIngredient(Long id, UserDetails userDetails) {
        log.info("User '{}' requested to delete ingredient with ID: {}", userDetails.getUsername(), id);

        Optional<Ingredient> opt = ingredientRepo.findById(id);
        if (opt.isEmpty()) {
            log.warn("Ingredient {} not found. Delete aborted for user: {}", id, userDetails.getUsername());
            return false;
        }

        Ingredient ingredient = opt.get();

        if (!ingredient.getUser().getUsername().equals(userDetails.getUsername())) {
            log.warn("User '{}' attempted to delete ingredient {} not owned by them", userDetails.getUsername(), id);
            throw new AccessDeniedException("You do not own this ingredient.");
        }

        ingredientRepo.deleteById(id);
        log.info("Ingredient {} successfully deleted by user: {}", id, userDetails.getUsername());
        return true;
    }
}

