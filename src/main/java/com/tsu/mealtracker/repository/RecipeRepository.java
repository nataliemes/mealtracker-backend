package com.tsu.mealtracker.repository;

import com.tsu.mealtracker.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}

