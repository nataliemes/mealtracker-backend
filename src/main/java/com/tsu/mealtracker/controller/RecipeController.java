package com.tsu.mealtracker.controller;

import com.tsu.mealtracker.dto.*;
import com.tsu.mealtracker.repository.IngredientRepository;
import com.tsu.mealtracker.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final IngredientRepository ingredientRepo;


    // *** POST ******************************************************************************* //

    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody @Valid RecipeForm recipeForm,
                                                  @AuthenticationPrincipal UserDetails userDetails) {

        log.info("POST /recipes called by user: {}", userDetails.getUsername());

        RecipeDTO createdDto = recipeService.createRecipe(recipeForm, userDetails.getUsername());

        log.info("Recipe created successfully by user {}: ID={}", userDetails.getUsername(), createdDto.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }



    // *** GET ******************************************************************************* //

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("GET /recipes called by user: {}", userDetails.getUsername());

        List<RecipeDTO> recipes = recipeService.getAllRecipes(userDetails);

        log.info("Returned {} recipes to user '{}'", recipes.size(), userDetails.getUsername());
        return ResponseEntity.ok(recipes);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserDetails userDetails) {

        log.info("GET /recipes/{} requested by user: {}", id, userDetails.getUsername());

        try {
            RecipeDTO dto = recipeService.getRecipeById(id, userDetails);
            log.info("Recipe id {} found for user '{}'", id, userDetails.getUsername());
            return ResponseEntity.ok(dto);
        } catch (AccessDeniedException ex) {
            log.warn("Access denied for user '{}' on recipe id {}", userDetails.getUsername(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            log.error("Recipe not found with id {} requested by user '{}'", id, userDetails.getUsername());
            return ResponseEntity.notFound().build();
        }
    }



    // *** PUT ******************************************************************************* //

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable Long id,
                                                  @RequestBody @Valid RecipeForm form,
                                                  @AuthenticationPrincipal UserDetails userDetails) {

        log.info("PUT /recipes/{} requested by user: {}", id, userDetails.getUsername());

        try {
            RecipeDTO updated = recipeService.updateRecipe(id, form, userDetails);
            log.info("Recipe id {} updated successfully by user '{}'", id, userDetails.getUsername());
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            log.warn("Access denied for user: {} when updating recipe {}", userDetails.getUsername(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            log.error("Recipe id {} not found, or update failed for user '{}'", id, userDetails.getUsername());
            return ResponseEntity.notFound().build();
        }
    }


    // *** DELETE ******************************************************************************* //

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id,
                                             @AuthenticationPrincipal UserDetails userDetails) {

        log.info("DELETE /recipes/{} requested by user: {}", id, userDetails.getUsername());

        boolean deleted = recipeService.deleteRecipe(id, userDetails);

        if (deleted) {
            log.info("Recipe {} successfully deleted by user: {}", id, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } else {
            log.warn("Recipe {} not found or not owned by user: {}", id, userDetails.getUsername());
            return ResponseEntity.notFound().build();
        }
    }


}
