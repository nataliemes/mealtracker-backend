package com.tsu.mealtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngredientForm {

    @NotBlank(message = "{ingredient.name.notblank}")
    private String name;

    @PositiveOrZero(message = "{ingredient.sugar.nonneg}")
    private double sugarPer100g;
}

