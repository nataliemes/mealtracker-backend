package com.tsu.mealtracker.service;

import com.tsu.mealtracker.model.Ingredient;
import com.tsu.mealtracker.model.Recipe;
import com.tsu.mealtracker.model.RecipeIngredient;
import com.tsu.mealtracker.repository.IngredientRepository;
import com.tsu.mealtracker.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;



@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepo;
    private final IngredientRepository ingredientRepo;


    // *** CREATE ******************************************************************************* //

    public Recipe createRecipe(Recipe recipe) {
        for (RecipeIngredient ri : recipe.getIngredients()) {

            Long ingredientId = ri.getIngredient().getId();
            Ingredient ingredient = ingredientRepo.findById(ingredientId)
                    .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + ingredientId));

            ri.setIngredient(ingredient);
            ri.setRecipe(recipe);
        }
        return recipeRepo.save(recipe);
    }


    // *** GET ******************************************************************************* //

    public List<Recipe> getAllRecipes() {
        return recipeRepo.findAll();
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepo.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
    }


    // *** UPDATE ******************************************************************************* //

    public Recipe updateRecipe(Long id, Recipe updatedRecipe) {
        return recipeRepo.findById(id).map(existing -> {
            existing.setName(updatedRecipe.getName());

            existing.getIngredients().clear();

            for (RecipeIngredient ri : updatedRecipe.getIngredients()) {
                ri.setRecipe(existing);
                existing.getIngredients().add(ri);
            }

            return recipeRepo.save(existing);
        }).orElse(null);
    }


    // *** DELETE ******************************************************************************* //

    public boolean deleteRecipe(Long id) {
        if (recipeRepo.existsById(id)) {
            recipeRepo.deleteById(id);
            return true;
        }
        return false;
    }



//    public double calculateTotalSugar(Recipe recipe) {
//        return recipe.getIngredients().stream()
//                .mapToDouble(ri -> ri.getIngredient().getSugarPer100g() * ri.getAmountInGrams() / 100.0)
//                .sum();
//    }

}


