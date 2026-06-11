package com.bustracker.security;

import com.bustracker.entity.User;
import com.bustracker.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom implementation of Spring Security's {@link UserDetailsService}.
 *
 * <p>This class bridges the gap between our {@link User} entity and
 * Spring Security's authentication mechanism. When a user attempts
 * to log in, Spring Security calls {@code loadUserByUsername} to
 * retrieve the user's credentials and authorities from the database.</p>
 *
 * <p>The "username" in our system is the user's email address.</p>
 *
 * <p>This follows the <strong>Adapter Pattern</strong> — adapting our
 * domain model (User entity) to the interface Spring Security expects
 * (UserDetails).</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email address for Spring Security authentication.
     *
     * @param email the user's email (used as username)
     * @return a UserDetails object containing credentials and authorities
     * @throws UsernameNotFoundException if no user exists with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));

        // Convert our UserRole enum to a Spring Security GrantedAuthority
        // Prefix with "ROLE_" as required by Spring Security's hasRole() method
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
