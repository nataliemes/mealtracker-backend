package com.tsu.mealtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class RecipeIngredientDTO {
    private Long ingredientId;
    private String ingredientName;
    private double quantity;
}
