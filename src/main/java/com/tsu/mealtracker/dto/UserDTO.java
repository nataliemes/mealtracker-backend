package com.tsu.mealtracker.dto;

import lombok.Data;
import java.util.List;


@Data
public class UserDTO {
    private String username;
    private List<String> authorities;
}
