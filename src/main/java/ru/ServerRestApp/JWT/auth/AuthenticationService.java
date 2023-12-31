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
import ru.ServerRestApp.JWT.Request.AuthenticationRequest;
import ru.ServerRestApp.JWT.Request.ForgotPasswordRequest;
import ru.ServerRestApp.JWT.Request.RegisterRequest;
import ru.ServerRestApp.JWT.Response.AuthenticationResponse;
import ru.ServerRestApp.JWT.Response.ForgotPasswordResponse;
import ru.ServerRestApp.JWT.config.JwtService;
import ru.ServerRestApp.repositories.TokensRepository;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.models.Tokens;
import ru.ServerRestApp.repositories.PeopleRepository;
import ru.ServerRestApp.repositories.TeamsRepository;
import ru.ServerRestApp.services.EmailSenderService;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;
import ru.ServerRestApp.util.PersonUtil;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service

public class AuthenticationService {

    private final PeopleRepository repository;
    private final TokensRepository tokensRepository;
    private final TeamsRepository teamsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private String refreshToken;
    private final EmailSenderService emailSenderService;
    private final PersonUtil personUtil;
    private final PeopleService peopleService;

    @Autowired
    public AuthenticationService(PeopleRepository repository, TokensRepository tokensRepository, TeamsRepository teamsRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, EmailSenderService emailSenderService, PersonUtil personUtil, PeopleService peopleService) {
        this.repository = repository;
        this.tokensRepository = tokensRepository;
        this.teamsRepository = teamsRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailSenderService = emailSenderService;
        this.personUtil = personUtil;
        this.peopleService = peopleService;
    }

    //Регистрация пользователя
    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {

            var person = Person.builder()
                    .full_name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .balance(0)
                    .gender(request.getGender())
                    .role("ROLE_LEADER")
                    .confirmed("F")
                    .build();

            Optional<Person> p = peopleService.findByEmail(person.getEmail());
            if (p.isPresent() && "F".equals(p.get().getConfirmed())) {
                teamsRepository.delete(p.get().getTeam());
                peopleService.delete(p.get().getId());
            }

            int person_id = repository.save(person).getId();

            var teams = Team.builder()
                    .name("Новая группа")
                    .build();
            int id = teamsRepository.save(teams).getId();
            Optional<Team> team = teamsRepository.findById(teams.getId());
            team.get().setName("Группа " + id);
            teamsRepository.save(team.get());

            Person person1 = peopleService.findById(person_id).get();
            person1.setTeam(teams);
            peopleService.save(person);


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
            cookie.setMaxAge(1814400);
            response.addCookie(cookie);

            emailSenderService.sendEmail(person.getEmail(), "Подтверждение аккаунта",
                "Для подтверждения аккаунта перейдите по ссылке: http://localhost:8080/api/v1/auth/confirmRegistration?token=" + accessToken);

            return AuthenticationResponse.builder()
                    .token(accessToken)
                    .person(person)
                    .build();
    }

    //Подтверждение регистрации
    public AuthenticationResponse confirmRegistration(String token, HttpServletResponse response) {

        Person person = personUtil.getPersonByTokenNew(token);

        if ("F".equals(person.getConfirmed()))
            person.setConfirmed("T");

        peopleService.save(person);

        return AuthenticationResponse.builder()
                .token(token)
                .person(person)
                .build();
    }

    //Аутентификация(подключение) к учётной записи
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

            if ("F".equals(person.getConfirmed()))
                throw new DataException("Пользователь не подтвердил регистрацию");

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
            cookie.setMaxAge(1814400);
            response.addCookie(cookie);
            return AuthenticationResponse.builder()
                    .token(accessToken)
                    .person(person)
                    .build();
        }
        catch (NoSuchElementException e){ return AuthenticationResponse.builder().error("Такого пользователя не существует").build(); }
        catch (ResponseStatusException e) { return AuthenticationResponse.builder().error("Неправильно введён логин или пароль").build(); }
    }

    //Запрос на случай, если пользователь забыл пароль
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request, HttpServletResponse response) {

        Optional<Person> found_person = repository.findByEmail(request.getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Такого пользователя не существует");
        Person person = found_person.get();

        if ("F".equals(person.getConfirmed()))
            throw new DataException("Пользователь не подтвердил регистрацию");

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

        emailSenderService.sendEmail(person.getEmail(), "Изменение пароля",
                "Для смены пароля перейдите по ссылке: http://localhost:5173/authorization/reset?token=" + accessToken);

        return ForgotPasswordResponse.builder()
                .token(accessToken)
                .person(person)
                .build();
    }

    //Подтверждение запроса, если пользователь забыл пароль
    public ForgotPasswordResponse forgotPasswordConfirm(ForgotPasswordRequest request, HttpServletResponse response) {

        Person person = personUtil.getPersonByTokenNew(request.getToken());

        if ("F".equals(person.getConfirmed()))
            throw new DataException("Пользователь не подтвердил регистрацию");

        /*
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

         */

        person.setPassword(passwordEncoder.encode(request.getPassword()));

        peopleService.save(person);

        return ForgotPasswordResponse.builder()
                .token(request.getToken())
                .person(person)
                .build();
    }

    //Обновить access и refresh токены
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
                    cookie.setMaxAge(1814400);
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
    //Очистить Cookie
    public AuthenticationResponse logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return null;
    }
}
