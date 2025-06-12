package com.tsu.mealtracker.controller;

import com.tsu.mealtracker.dto.*;
import com.tsu.mealtracker.model.Recipe;
import com.tsu.mealtracker.model.RecipeIngredient;
import com.tsu.mealtracker.repository.IngredientRepository;
import com.tsu.mealtracker.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final IngredientRepository ingredientRepo;


    // *** POST ******************************************************************************* //

    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody @Valid RecipeForm recipeForm, Principal principal) {

        String username = principal.getName();

        RecipeDTO responseDto = recipeService.createRecipe(recipeForm, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }



    // *** GET ******************************************************************************* //

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes(@AuthenticationPrincipal UserDetails userDetails) {
        List<RecipeDTO> recipes = recipeService.getAllRecipes(userDetails);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        try {
            RecipeDTO dto = recipeService.getRecipeById(id, userDetails);
            return ResponseEntity.ok(dto);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }



    // *** PUT ******************************************************************************* //

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable Long id,
                                                  @RequestBody @Valid RecipeForm form,
                                                  @AuthenticationPrincipal UserDetails userDetails) {

        // TO-DO: add binding result check

        try {
            RecipeDTO updated = recipeService.updateRecipe(id, form, userDetails);
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // *** DELETE ******************************************************************************* //

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        return recipeService.deleteRecipe(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}
