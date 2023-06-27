package ru.ServerRestApp.JWT.auth;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.ServerRestApp.JWT.config.JwtService;
import ru.ServerRestApp.JWT.repository.TokensRepository;
import ru.ServerRestApp.JWT.repository.UserRepository;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.Tokens;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service

public class AuthenticationService {

    private final UserRepository repository;

    private final TokensRepository tokensRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private String refreshToken;

    public static boolean Auth = false;

    @Autowired
    public AuthenticationService(UserRepository repository, TokensRepository tokensRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.tokensRepository = tokensRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;

    }

    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {
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
            Optional<Tokens> token = tokensRepository.findByEmail(person.getEmail());
            if (token.isPresent()){ tokensRepository.deleteById(token.get().getId()); }
            var tokens = Tokens.builder()
                    .email(person.getEmail())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            tokensRepository.save(tokens);
            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(500000);
            response.addCookie(cookie);
            Auth = true;
            return AuthenticationResponse.builder()
                    .token(accessToken)
                    .person(person)
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
            Optional<Tokens> token = tokensRepository.findByEmail(person.getEmail());
            if (token.isPresent()){
                token.get().setAccessToken(accessToken);
                token.get().setRefreshToken(refreshToken);
                tokensRepository.save(token.get());
            }
            else {
                var tokens = Tokens.builder()
                        .email(person.getEmail())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                tokensRepository.save(tokens);
            }

            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(500000);
            response.addCookie(cookie);
            Auth = true;
            return AuthenticationResponse.builder()
                    .token(accessToken)
                    .person(person)
                    .build();
        }
        catch (NoSuchElementException e){ return AuthenticationResponse.builder().error("Такого пользователя не существует").build(); }
        catch (ResponseStatusException e) { return AuthenticationResponse.builder().error("Неправильно введён логин или пароль").build(); }
    }

    /*public AuthenticationResponse getAccessToken(String refreshToken) {
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
    }*/

    public AuthenticationResponse refresh(String refreshToken, HttpServletResponse response){
        try {
            if (JwtService.validateRefreshToken(refreshToken)) {
                final Claims claims = JwtService.getRefreshClaims(refreshToken);
                final String login = claims.getSubject();
                Optional<Tokens> tokens = tokensRepository.findByEmail(login);
                final String saveRefreshToken = tokens.get().getRefreshtoken();
                System.out.println(saveRefreshToken);
                if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                    final Person person = repository.findByEmail(login)
                            .orElseThrow();
                    final String accessToken = JwtService.generateToken(person);
                    final String newRefreshToken = JwtService.generateRefreshToken(person);
                    if (tokens.isPresent()){
                        tokens.get().setAccessToken(accessToken);
                        tokens.get().setRefreshToken(newRefreshToken);
                        tokensRepository.save(tokens.get());
                    }
                    Cookie cookie = new Cookie("refreshToken", newRefreshToken);
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    cookie.setMaxAge(500000);
                    response.addCookie(cookie);
                    return AuthenticationResponse.builder()
                            .token(accessToken)
                            .person(person)
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

    public AuthenticationResponse logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        Auth = false;
        return null;
    }
}
