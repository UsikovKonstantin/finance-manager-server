package ru.ServerRestApp.JWT.auth;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ServerRestApp.JWT.config.JwtService;
import ru.ServerRestApp.JWT.repository.UserRepository;
import ru.ServerRestApp.models.Person;

import java.util.HashMap;
import java.util.Map;

@Service

public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private String refreshToken;

    @Autowired
    public AuthenticationService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;

    }

    public AuthenticationResponse register(RegisterRequest request) {
        var person = Person.builder()
                .team(request.getTeam())
                .full_name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .balance(request.getBalance())
                .gender(request.getGender())
                .role(request.getRole())
                .build();
        repository.save(person);
        final String accessToken = jwtService.generateToken(person);
        final String refreshToken  = jwtService.generateRefreshToken(person);
        refreshStorage.put(person.getEmail(), refreshToken);
        return AuthenticationResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var person = repository.findByEmail(request.getEmail())
                .orElseThrow();
        final String accessToken = jwtService.generateToken(person);
        refreshToken  = jwtService.generateRefreshToken(person);
        refreshStorage.put(person.getEmail(), refreshToken);
        return AuthenticationResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse getAccessToken(String refreshToken) {
        if (JwtService.validateRefreshToken(refreshToken)) {
            final Claims claims = JwtService.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final Person user = repository.findByEmail(login)
                        .orElseThrow();
                final String accessToken = JwtService.generateToken(user);
                return AuthenticationResponse.builder()
                        .token(accessToken)
                        .refreshToken(null)
                        .build();
            }
        }
        return AuthenticationResponse.builder()
                .token(null)
                .refreshToken(null)
                .build();
    }

    public AuthenticationResponse refresh(String refreshToken) throws AuthException {
        if (JwtService.validateRefreshToken(refreshToken)) {
            final Claims claims = JwtService.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final Person user = repository.findByEmail(login)
                        .orElseThrow();
                final String accessToken = JwtService.generateToken(user);
                final String newRefreshToken = JwtService.generateRefreshToken(user);
                refreshStorage.put(user.getEmail(), newRefreshToken);
                return AuthenticationResponse.builder()
                        .token(accessToken)
                        .refreshToken(newRefreshToken)
                        .build();
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }
}
