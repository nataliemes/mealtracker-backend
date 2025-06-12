package com.tsu.mealtracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecipeForm {
    @NotBlank
    private String name;

    @Valid
    @Size(min = 1, message = "Recipe must have at least one ingredient")
    private List<RecipeIngredientForm> ingredients = new ArrayList<>();
}
