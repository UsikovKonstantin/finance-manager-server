package ru.ServerRestApp.JWT.auth;

import io.jsonwebtoken.*;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.pattern.PatternParseException;
import ru.ServerRestApp.JWT.config.JwtService;
import ru.ServerRestApp.JWT.repository.UserRepository;
import ru.ServerRestApp.models.Person;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
                    .cookie(new Cookie("refreshToken", refreshToken))
                    .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        try {
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
            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(500000);
            response.addCookie(cookie);
            return AuthenticationResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .cookie(cookie)
                    .build();
        }
        catch (NoSuchElementException e){ return AuthenticationResponse.builder().error("Такого пользователя не существует").build(); }
        catch (ResponseStatusException e) { return AuthenticationResponse.builder().error("Неправильно введён логин или пароль").build(); }
    }

    public AuthenticationResponse getAccessToken(String refreshToken) {
        try {
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
                            .cookie(new Cookie("refreshToken", null))
                            .build();
                }
                else {
                    return AuthenticationResponse.builder().error("Такого рефреш токена не существует").build();
                }
            }
        }
        catch (ExpiredJwtException e) { return AuthenticationResponse.builder().error("Срок действия токена истек").build(); }
        catch (UnsupportedJwtException e) { return AuthenticationResponse.builder().error("Неподдерживаемый jwt").build(); }
        catch (MalformedJwtException e) { return AuthenticationResponse.builder().error("Деформированный jwt").build();}
        catch (SignatureException e) { return AuthenticationResponse.builder().error("Недействительная подпись").build(); }
        catch (Exception e) { return AuthenticationResponse.builder().error("Невалидный токен").build(); }
        return AuthenticationResponse.builder().build();
    }

    public AuthenticationResponse refresh(String refreshToken, HttpServletResponse response) throws AuthException {
        try {
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
                    Cookie cookie = new Cookie("refreshToken", refreshToken);
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    cookie.setMaxAge(500000);
                    response.addCookie(cookie);
                    return AuthenticationResponse.builder()
                            .token(accessToken)
                            .refreshToken(newRefreshToken)
                            .cookie(new Cookie("refreshToken", newRefreshToken))
                            .build();
                }
                else return AuthenticationResponse.builder().error("Такого рефреш токена не существует").build();
            }
        }
        catch (ExpiredJwtException e) { return AuthenticationResponse.builder().error("Срок действия токена истек").build(); }
        catch (UnsupportedJwtException e) { return AuthenticationResponse.builder().error("Неподдерживаемый jwt").build(); }
        catch (MalformedJwtException e) { return AuthenticationResponse.builder().error("Деформированный jwt").build();}
        catch (SignatureException e) { return AuthenticationResponse.builder().error("Недействительная подпись").build(); }
        catch (Exception e) { return AuthenticationResponse.builder().error("Невалидный токен").build(); }
        return AuthenticationResponse.builder().build();
    }
}
