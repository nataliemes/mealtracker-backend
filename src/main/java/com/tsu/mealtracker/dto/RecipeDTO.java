package com.tsu.mealtracker.dto;

import com.tsu.mealtracker.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;



@Data
@AllArgsConstructor
public class RecipeDTO {
    private Long id;
    private String name;
    private List<RecipeIngredientDTO> ingredients;

}
