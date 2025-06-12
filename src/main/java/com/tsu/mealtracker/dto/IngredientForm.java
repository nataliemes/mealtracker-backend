package com.tsu.mealtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data

public class IngredientForm {

    @NotBlank(message = "{ingredient.name.notblank}")
    private String name;

    @PositiveOrZero(message = "{ingredient.sugar.nonneg}")
    private double sugarPer100g;
}

