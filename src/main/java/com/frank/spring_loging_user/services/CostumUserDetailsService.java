package com.frank.spring_loging_user.services;

import com.frank.spring_loging_user.entities.User;
import com.frank.spring_loging_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service // Indique que cette classe est un service Spring géré par le conteneur IoC
@RequiredArgsConstructor // Génère un constructeur avec les dépendances finales automatiquement
public class CostumUserDetailsService implements UserDetailsService {

    // Injection du repository User pour récupérer les informations sur l'utilisateur depuis la base de données
    private final UserRepository userRepository;

    /**
     * Méthode principale de Spring Security pour charger les informations de l'utilisateur lors du processus d'authentification.
     * Cette méthode est appelée par Spring Security pour récupérer un utilisateur en fonction de son nom d'utilisateur (username).
     *
     * @param username Le nom d'utilisateur à rechercher dans la base de données.
     * @return Un objet UserDetails qui contient les informations de l'utilisateur.
     * @throws UsernameNotFoundException Si l'utilisateur avec ce nom d'utilisateur n'existe pas dans la base de données.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Cherche l'utilisateur dans la base de données en utilisant le repository
        User user = userRepository.findByUsername(username);

        // Si l'utilisateur n'est pas trouvé, on lève une exception (Spring Security l'interceptera)
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Retourne un objet UserDetails avec les informations de l'utilisateur
        // On utilise 'SimpleGrantedAuthority' pour transformer le rôle de l'utilisateur en un objet autorité
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),  // Nom d'utilisateur
                user.getPassword(),  // Mot de passe
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())) // Liste des rôles (ici un seul rôle)
        );
    }
}
