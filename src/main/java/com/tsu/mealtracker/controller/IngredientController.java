package com.tsu.mealtracker.controller;

import com.tsu.mealtracker.dto.IngredientDTO;
import com.tsu.mealtracker.dto.IngredientForm;
import com.tsu.mealtracker.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;


    // *** POST ******************************************************************************* //

    @PostMapping
    public ResponseEntity<?> createIngredient(
            @Valid @RequestBody IngredientForm ingredientForm,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("POST /ingredients called by user: {}", userDetails.getUsername());

        IngredientDTO createdDto = ingredientService.createIngredient(ingredientForm, userDetails.getUsername());

        log.info("Ingredient created successfully by user {}: ID={}", userDetails.getUsername(), createdDto.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }


    // *** GET ******************************************************************************* //

    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getAllIngredients(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("GET /ingredients called by user: {}", userDetails.getUsername());

        List<IngredientDTO> ingredients = ingredientService.getAllIngredients(userDetails);

        log.info("Returned {} ingredients for user: {}", ingredients.size(), userDetails.getUsername());
        return ResponseEntity.ok(ingredients);
    }


    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getIngredientById(@PathVariable Long id,
                                                           @AuthenticationPrincipal UserDetails userDetails) {

        log.info("GET /ingredients/{} requested by user: {}", id, userDetails.getUsername());

        try {
            IngredientDTO dto = ingredientService.getIngredientById(id, userDetails);
            log.info("Ingredient id {} found for user: {}", id, userDetails.getUsername());
            return ResponseEntity.ok(dto);
        } catch (AccessDeniedException ex) {
            log.warn("Access denied for user: {} on ingredient id {}", userDetails.getUsername(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            log.warn("Ingredient not found with id {} requested by user: {}", id, userDetails.getUsername());
            return ResponseEntity.notFound().build();
        }
    }



    // *** PUT ******************************************************************************* //

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTO> updateIngredient(@PathVariable Long id,
                                                          @RequestBody @Valid IngredientForm form,
                                                          @AuthenticationPrincipal UserDetails userDetails) {

        log.info("PUT /ingredients/{} requested by user: {}", id, userDetails.getUsername());

        try {
            IngredientDTO updated = ingredientService.updateIngredient(id, form, userDetails);
            log.info("Ingredient id {} updated successfully by user: {}", id, userDetails.getUsername());
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            log.warn("Access denied for user: {} when updating ingredient {}", userDetails.getUsername(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            log.warn("Ingredient id {} not found, or update failed for user: {}", id, userDetails.getUsername());
            return ResponseEntity.notFound().build();
        }
    }


    // *** DELETE ******************************************************************************* //

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserDetails userDetails) {

        log.info("DELETE /ingredients/{} requested by user: {}", id, userDetails.getUsername());

        boolean deleted = ingredientService.deleteIngredient(id, userDetails);

        if (deleted) {
            log.info("Ingredient {} successfully deleted by user: {}", id, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } else {
            log.warn("Ingredient {} not found, or not owned by user: {}", id, userDetails.getUsername());
            return ResponseEntity.notFound().build();
        }
    }

}


