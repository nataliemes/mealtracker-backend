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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Recipe recipe = new Recipe();
        recipe.setName(form.getName());
        recipe.setUser(user);

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        for (RecipeIngredientForm riForm : form.getIngredients()) {

            Ingredient ingredient = ingredientRepo.findById(riForm.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + riForm.getIngredientId()));

            RecipeIngredient ri = new RecipeIngredient();
            ri.setIngredient(ingredient);
            ri.setQuantity(riForm.getQuantity());
            ri.setRecipe(recipe);

            recipeIngredients.add(ri);
        }

        recipe.setIngredients(recipeIngredients);

        recipeRepo.save(recipe);
        return toDto(recipe);
    }



    // *** GET ******************************************************************************* //

    public List<RecipeDTO> getAllRecipes(UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Recipe> recipes;

        if (isAdmin) {
            recipes = recipeRepo.findAll();
        } else {
            recipes = recipeRepo.findByUserUsername(userDetails.getUsername());
        }

        return recipes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    public RecipeDTO getRecipeById(Long id, UserDetails userDetails) {
        Recipe recipe = recipeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        // admin can access everything
        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            if (!recipe.getUser().getUsername().equals(userDetails.getUsername())) {
                throw new AccessDeniedException("You don't have permission to view this recipe");
            }
        }
        return toDto(recipe);
    }


    // *** UPDATE ******************************************************************************* //

    public RecipeDTO updateRecipe(Long id, RecipeForm form, UserDetails userDetails) {

        Recipe recipe = recipeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));


        // recipe can only be updated by admin or the creator
        if (!isAdmin && !recipe.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You are not allowed to update this recipe");
        }


        // TO-DO: maybe add a function that takes ingredients list from recipe (for Form to Recipe conversion)

        List<RecipeIngredient> ingredients = new ArrayList<>();

        for (RecipeIngredientForm riForm : form.getIngredients()) {

            Ingredient ingredient = ingredientRepo.findById(riForm.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + riForm.getIngredientId()));

            RecipeIngredient ri = new RecipeIngredient();
            ri.setIngredient(ingredient);
            ri.setQuantity(riForm.getQuantity());
            ri.setRecipe(recipe);

            ingredients.add(ri);
        }

        recipe.setName(form.getName());
        recipe.setIngredients(ingredients);

        Recipe saved = recipeRepo.save(recipe);
        return toDto(saved);
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
//                .mapToDouble(ri -> ri.getIngredient().getSugarPer100g() * ri.getQuantity() / 100.0)
//                .sum();
//    }

}


