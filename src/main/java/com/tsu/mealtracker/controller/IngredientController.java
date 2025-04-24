package com.tsu.mealtracker.controller;

import com.tsu.mealtracker.model.Ingredient;
import com.tsu.mealtracker.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;


    @PostMapping
    public ResponseEntity<Ingredient> createIngredient(@RequestBody @Valid Ingredient ingredient) {
        Ingredient savedIngredient = ingredientService.createIngredient(ingredient);
        return ResponseEntity.ok(savedIngredient);
    }


    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }


    @GetMapping("/{id}")
    public Ingredient getIngredientById(@PathVariable Long id) {
        return ingredientService.getIngredientById(id);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Long id, @RequestBody @Valid Ingredient ingredient) {
        Ingredient updatedIngredient = ingredientService.updateIngredient(id, ingredient);
        return updatedIngredient != null
                ? ResponseEntity.ok(updatedIngredient)
                : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        return ingredientService.deleteIngredient(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}


