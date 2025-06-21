package com.tsu.mealtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterForm {

    @NotBlank(message = "{username.notblank}")
    @Size(min = 3, message = "{username.size}")
    private String username;

    @NotBlank(message = "{password.notblank}")
    @Size(min = 6, message = "{password.size}")
    private String password;
}
