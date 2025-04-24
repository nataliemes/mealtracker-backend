package com.tsu.mealtracker.controller;

import com.tsu.mealtracker.model.Recipe;
import com.tsu.mealtracker.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;


    @PostMapping
    public ResponseEntity<Recipe> addRecipe(@RequestBody @Valid Recipe recipe) {
        Recipe savedRecipe = recipeService.createRecipe(recipe);
        return ResponseEntity.ok(savedRecipe);
    }


    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }


    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody @Valid Recipe recipe) {
        Recipe updatedRecipe = recipeService.updateRecipe(id, recipe);
        return updatedRecipe != null
                ? ResponseEntity.ok(updatedRecipe)
                : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        return recipeService.deleteRecipe(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
