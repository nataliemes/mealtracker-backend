package com.tsu.mealtracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LoginForm {

    @NotBlank(message = "{username.notblank}")
    private String username;

    @NotBlank(message = "{password.notblank}")
    private String password;
}
