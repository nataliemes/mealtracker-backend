package com.tsu.mealtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;


@Data
@AllArgsConstructor
public class RecipeDTO {
    private Long id;
    private String name;
    private List<RecipeIngredientDTO> ingredients;
}
