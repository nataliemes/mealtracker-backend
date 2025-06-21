package com.tsu.mealtracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RecipeIngredientForm {

    @NotNull
    private Long ingredientId;  // Selected from user's ingredients

    @Positive(message = "{ingredient.quantity}")
    private double quantity;
}
