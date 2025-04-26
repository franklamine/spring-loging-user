package com.frank.spring_loging_user.controller;

import com.frank.spring_loging_user.entities.User;
import com.frank.spring_loging_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class RegistrationLoginController {

    // Injecte le repository qui permet d'interagir avec la base de données des utilisateurs
    private final UserRepository userRepository;

    // Injecte l'encodeur de mots de passe (BCrypt)
    private final PasswordEncoder passwordEncoder;

    // Injecte le gestionnaire d'authentification (utilisé pour vérifier les identifiants)
    private final AuthenticationManager authenticationManager;

    // Endpoint POST pour l'inscription d'un utilisateur
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Vérifie si le nom d'utilisateur existe déjà
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username is already in use");
        }

        // Encode le mot de passe avant de l'enregistrer en BDD
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Enregistre l'utilisateur et retourne une réponse 200 avec l'utilisateur
        return ResponseEntity.ok(userRepository.save(user));
    }

    // Endpoint POST pour connecter un utilisateur
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        try {
            // Vérifie les identifiants avec le gestionnaire d'authentification
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            // Si les identifiants sont corrects, on retourne 200 OK
            return ResponseEntity.ok("User logged in");
        } catch (Exception e) {
            // Si l’authentification échoue, on retourne une erreur 401 (non autorisé)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}

