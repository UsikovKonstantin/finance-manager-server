package ru.ServerRestApp.JWT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.Tokens;

import java.util.Optional;

public interface TokensRepository extends JpaRepository<Tokens, Integer> {
    Optional<Tokens> findByEmail(String email);
    Optional<Tokens> findByAccessToken(String accessToken);
}
