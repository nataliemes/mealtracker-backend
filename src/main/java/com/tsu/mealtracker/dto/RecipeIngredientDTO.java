package com.tsu.mealtracker.dto;


import com.tsu.mealtracker.model.Ingredient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class RecipeIngredientDTO {
    private Long ingredientId;
    private String ingredientName;
    private double quantity;
}
