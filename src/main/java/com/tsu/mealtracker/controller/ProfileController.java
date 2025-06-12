package com.tsu.mealtracker.controller;

import com.tsu.mealtracker.dto.IngredientDTO;
import com.tsu.mealtracker.dto.RecipeDTO;
import com.tsu.mealtracker.dto.RecipeIngredientDTO;
import com.tsu.mealtracker.repository.IngredientRepository;
import com.tsu.mealtracker.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final RecipeRepository recipeRepo;
    private final IngredientRepository ingredientRepo;

    @GetMapping("/profile")
    public String showProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {

//        List<RecipeDTO> recipeDTOs = recipeRepo.findByUserUsername(userDetails.getUsername()).stream()
//                .map(recipe -> {
//                    RecipeDTO recipeDTO = new RecipeDTO();
//                    recipeDTO.setName(recipe.getName());
//
//                    List<RecipeIngredientDTO> ingredientDTOs = recipe.getIngredients().stream()
//                            .map(ri -> {
//                                RecipeIngredientDTO riDTO = new RecipeIngredientDTO();
//                                riDTO.setQuantity(ri.getQuantity());
//                                riDTO.setIngredientId(ri.getIngredient().getId());
//                                return riDTO;
//                            })
//                            .collect(Collectors.toList());
//
//                    recipeDTO.setIngredients(ingredientDTOs);
//                    return recipeDTO;
//                })
//                .collect(Collectors.toList());
//
//        model.addAttribute("recipes", recipeDTOs);
//
//        List<IngredientDTO> ingredientDTOs = ingredientRepo.findByUserUsername(userDetails.getUsername()).stream()
//                .map(i -> new IngredientDTO(i.getName(), i.getSugarPer100g()))
//                .collect(Collectors.toList());
//
//        model.addAttribute("ingredients", ingredientDTOs);

        return "profile/homepage";
    }

}


