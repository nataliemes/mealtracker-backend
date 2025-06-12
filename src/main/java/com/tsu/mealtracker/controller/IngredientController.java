package com.tsu.mealtracker.controller;

import com.tsu.mealtracker.dto.IngredientDTO;
import com.tsu.mealtracker.dto.IngredientForm;
import com.tsu.mealtracker.model.Ingredient;
import com.tsu.mealtracker.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;


    // *** POST ******************************************************************************* //

    @PostMapping
    public ResponseEntity<?> createIngredient(@Valid @RequestBody IngredientForm ingredientForm,
                                              BindingResult result,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        if (result.hasErrors()) {

            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            fe -> {
                                String msg = fe.getDefaultMessage();
                                return msg != null ? msg : "Invalid value";
                            }
                    ));


            return ResponseEntity.badRequest().body(errors);
        }

        IngredientDTO createdDto = ingredientService.createIngredient(ingredientForm, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }


    // *** GET ******************************************************************************* //

    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getAllIngredients(@AuthenticationPrincipal UserDetails userDetails) {
        List<IngredientDTO> ingredients = ingredientService.getAllIngredients(userDetails);
        return ResponseEntity.ok(ingredients);
    }


    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getIngredientById(@PathVariable Long id,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            IngredientDTO dto = ingredientService.getIngredientById(id, userDetails);
            return ResponseEntity.ok(dto);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }



    // *** PUT ******************************************************************************* //

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTO> updateIngredient(@PathVariable Long id,
                                                          @RequestBody @Valid IngredientForm form,
                                                          @AuthenticationPrincipal UserDetails userDetails) {

        // TO-DO: add binding result check

        try {
            IngredientDTO updated = ingredientService.updateIngredient(id, form, userDetails);
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // *** DELETE ******************************************************************************* //

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        return ingredientService.deleteIngredient(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }


}


