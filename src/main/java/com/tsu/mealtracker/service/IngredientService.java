package com.tsu.mealtracker.service;

import com.tsu.mealtracker.model.Ingredient;
import com.tsu.mealtracker.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepo;


    // *** CREATE ******************************************************************************* //

    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepo.save(ingredient);
    }


    // *** GET ******************************************************************************* //

    public List<Ingredient> getAllIngredients() {
        return ingredientRepo.findAll();
    }

    public Ingredient getIngredientById(Long id) {
        return ingredientRepo.findById(id).orElseThrow(() -> new RuntimeException("Ingredient not found"));
    }


    // *** UPDATE ******************************************************************************* //

    public Ingredient updateIngredient(Long id, Ingredient ingredient) {
        if (ingredientRepo.existsById(id)) {
            ingredient.setId(id);
            return ingredientRepo.save(ingredient);
        }
        return null;
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

