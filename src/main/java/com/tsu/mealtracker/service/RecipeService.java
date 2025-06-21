package com.tsu.mealtracker.service;

import com.tsu.mealtracker.dto.*;
import com.tsu.mealtracker.model.Ingredient;
import com.tsu.mealtracker.model.Recipe;
import com.tsu.mealtracker.model.RecipeIngredient;
import com.tsu.mealtracker.model.User;
import com.tsu.mealtracker.repository.IngredientRepository;
import com.tsu.mealtracker.repository.RecipeRepository;
import com.tsu.mealtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepo;
    private final IngredientRepository ingredientRepo;
    private final UserRepository userRepo;


    public RecipeDTO toDto(Recipe recipe) {
        List<RecipeIngredientDTO> riDTOList = new ArrayList<>();
        for (RecipeIngredient ri : recipe.getIngredients()) {
            RecipeIngredientDTO riDto = new RecipeIngredientDTO(ri.getIngredient().getId(), ri.getIngredient().getName(), ri.getQuantity());
            riDTOList.add(riDto);
        }

        return new RecipeDTO(recipe.getId(), recipe.getName(), riDTOList);
    }



    // *** CREATE ******************************************************************************* //

    public RecipeDTO createRecipe(RecipeForm form, String username) {
        log.info("Creating recipe '{}' for user '{}'", form.getName(), username);

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        Recipe recipe = new Recipe();
        recipe.setName(form.getName());
        recipe.setUser(user);

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        for (RecipeIngredientForm riForm : form.getIngredients()) {
            long ingredientId = riForm.getIngredientId();
            log.debug("Processing ingredient id: {} with quantity {}", ingredientId, riForm.getQuantity());

            Ingredient ingredient = ingredientRepo.findById(ingredientId)
                    .orElseThrow(() -> {
                        log.error("Ingredient not found with id: {}", ingredientId);
                        return new RuntimeException("Ingredient not found with id: " + ingredientId);
                    });

            RecipeIngredient ri = new RecipeIngredient();
            ri.setIngredient(ingredient);
            ri.setQuantity(riForm.getQuantity());
            ri.setRecipe(recipe);

            recipeIngredients.add(ri);
        }

        recipe.setIngredients(recipeIngredients);

        recipeRepo.save(recipe);
        log.info("Recipe '{}' successfully created for user '{}', with {} ingredients", form.getName(), username, recipeIngredients.size());

        return toDto(recipe);
    }



    // *** GET ******************************************************************************* //

    public List<RecipeDTO> getAllRecipes(UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        log.info("Fetching all recipes for user '{}' (admin={})", userDetails.getUsername(), isAdmin);

        List<Recipe> recipes;

        if (isAdmin) {
            recipes = recipeRepo.findAll();
            log.debug("Admin access: retrieved {} recipes", recipes.size());
        } else {
            recipes = recipeRepo.findByUserUsername(userDetails.getUsername());
            log.debug("User access: retrieved {} recipes for user '{}'", recipes.size(), userDetails.getUsername());
        }

        List<RecipeDTO> dtoList = recipes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        log.info("Returning {} recipes to user '{}'", dtoList.size(), userDetails.getUsername());
        return dtoList;
    }


    public RecipeDTO getRecipeById(Long id, UserDetails userDetails) {
        log.info("User '{}' requested recipe with id {}", userDetails.getUsername(), id);

        Recipe recipe = recipeRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Recipe not found with id {}", id);
                    return new RuntimeException("Recipe not found");
                });

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            if (!recipe.getUser().getUsername().equals(userDetails.getUsername())) {
                log.warn("User '{}' tried to access recipe id {} without permission", userDetails.getUsername(), id);
                throw new AccessDeniedException("You don't have permission to view this recipe");
            }
        }

        log.info("Recipe id {} accessed by user '{}'", id, userDetails.getUsername());
        return toDto(recipe);
    }


    // *** UPDATE ******************************************************************************* //

    public RecipeDTO updateRecipe(Long id, RecipeForm form, UserDetails userDetails) {
        log.info("User '{}' requested update for recipe id {}", userDetails.getUsername(), id);

        Recipe recipe = recipeRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Recipe not found with id {}", id);
                    return new RuntimeException("Recipe not found");
                });

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !recipe.getUser().getUsername().equals(userDetails.getUsername())) {
            log.warn("User '{}' attempted to update recipe id {} without permission", userDetails.getUsername(), id);
            throw new AccessDeniedException("You are not allowed to update this recipe");
        }

        List<RecipeIngredient> ingredients = new ArrayList<>();

        for (RecipeIngredientForm riForm : form.getIngredients()) {
            long ingredientId = riForm.getIngredientId();
            log.debug("Updating ingredient id: {} with quantity {}", ingredientId, riForm.getQuantity());

            Ingredient ingredient = ingredientRepo.findById(ingredientId)
                    .orElseThrow(() -> {
                        log.error("Ingredient not found with id: {}", ingredientId);
                        return new RuntimeException("Ingredient not found with id: " + ingredientId);
                    });

            RecipeIngredient ri = new RecipeIngredient();
            ri.setIngredient(ingredient);
            ri.setQuantity(riForm.getQuantity());
            ri.setRecipe(recipe);

            ingredients.add(ri);
        }

        recipe.setName(form.getName());
        recipe.setIngredients(ingredients);

        Recipe saved = recipeRepo.save(recipe);
        log.info("Recipe id {} updated successfully by user '{}'", id, userDetails.getUsername());

        return toDto(saved);
    }



    // *** DELETE ******************************************************************************* //

    public boolean deleteRecipe(Long id, UserDetails userDetails) {
        log.info("User '{}' requested to delete recipe with ID: {}", userDetails.getUsername(), id);

        Optional<Recipe> opt = recipeRepo.findById(id);
        if (opt.isEmpty()) {
            log.warn("Recipe {} not found. Delete aborted for user: {}", id, userDetails.getUsername());
            return false;
        }

        Recipe recipe = opt.get();

        if (!recipe.getUser().getUsername().equals(userDetails.getUsername())) {
            log.warn("User '{}' attempted to delete recipe {} not owned by them", userDetails.getUsername(), id);
            throw new AccessDeniedException("You do not own this recipe.");
        }

        ingredientRepo.deleteById(id);
        log.info("Recipe {} successfully deleted by user: {}", id, userDetails.getUsername());
        return true;
    }



//    public double calculateTotalSugar(Recipe recipe) {
//        return recipe.getIngredients().stream()
//                .mapToDouble(ri -> ri.getIngredient().getSugarPer100g() * ri.getQuantity() / 100.0)
//                .sum();
//    }

}


