package com.bustracker.repository;

import com.bustracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entities.
 *
 * <p>Provides CRUD operations and custom finder methods for
 * user authentication and lookup.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * Used during authentication to load user credentials.
     *
     * @param email the user's email address
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email already exists.
     * Used during registration to prevent duplicate accounts.
     *
     * @param email the email to check
     * @return true if a user with this email exists
     */
    boolean existsByEmail(String email);
}
