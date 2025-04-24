package com.tsu.mealtracker.repository;

import com.tsu.mealtracker.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {



}

