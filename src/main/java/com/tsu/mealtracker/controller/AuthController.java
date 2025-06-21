package com.tsu.mealtracker.controller;


import com.tsu.mealtracker.dto.LoginForm;
import com.tsu.mealtracker.dto.RegisterForm;
import com.tsu.mealtracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;


import java.util.Locale;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final MessageSource messageSource;



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterForm form,
                                      Locale locale) {

        log.info("Received registration request for username: {}", form.getUsername());

        if (userService.existsUsername(form.getUsername())) {
            log.warn("Registration failed: Username '{}' already exists.", form.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", messageSource.getMessage("register.exists", null, locale)));
        }

        userService.register(form.getUsername(), form.getPassword());

        log.info("User '{}' registered successfully.", form.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", messageSource.getMessage("register.success", null, locale)));
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginForm loginRequest,
                                   HttpServletRequest request,
                                   Locale locale) {

        log.info("Received login request for username: {}", loginRequest.getUsername());

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            String successMessage = messageSource.getMessage("login.success", null, locale);
            log.info("User '{}' logged in successfully.", loginRequest.getUsername());
            return ResponseEntity.ok(Map.of("message", successMessage));

        } catch (BadCredentialsException ex) {
            log.error("Login failed for username '{}': Invalid credentials.", loginRequest.getUsername(), ex);
            String errorMessage = messageSource.getMessage("login.invalid", null, locale);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", errorMessage));
        }
    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Locale locale) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.info("Logging out user '{}'", auth.getName());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        String message = messageSource.getMessage("logout.success", null, locale);
        log.info("User logged out successfully.");
        return ResponseEntity.ok(Map.of("message", message));
    }
}

