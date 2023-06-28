package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.ServerRestApp.JWT.repository.TokensRepository;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.models.Tokens;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.services.TeamsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;
import ru.ServerRestApp.validators.PersonValidator;

import java.util.List;
import java.util.Optional;

import static ru.ServerRestApp.util.ErrorsUtil.returnDataErrorsToClient;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final TeamsService teamsService;
    private final PasswordEncoder passwordEncoder;
    private final PersonValidator personValidator;
    private final TokensRepository tokensRepository;
    @Autowired
    public PeopleController(PeopleService peopleService, TeamsService teamsService, PasswordEncoder passwordEncoder, PersonValidator personValidator, TokensRepository tokensRepository, TokensRepository tokensRepository1) {
        this.peopleService = peopleService;
        this.teamsService = teamsService;
        this.passwordEncoder = passwordEncoder;
        this.personValidator = personValidator;
        this.tokensRepository = tokensRepository1;
    }

    @GetMapping()
    public ResponseEntity<List<Person>> getAllPeople() {
        List<Person> people = peopleService.findAll();
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    @GetMapping("/team/{id}")
    public ResponseEntity<List<Person>> getPeopleByTeamId(@PathVariable("id") int id) {
        Optional<Team> team = teamsService.findById(id);
        if (team.isEmpty())
            throw new NotFoundException("Team with this id wasn't found!");

        List<Person> people = peopleService.findByTeamId(id);
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable("id") int id) {
        Optional<Person> person = peopleService.findById(id);
        if (person.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");
        return new ResponseEntity<>(person.get(), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Person> getPersonByEmail(@PathVariable("email") String email) {
        Optional<Person> person = peopleService.findByEmail(email);
        if (person.isEmpty())
            throw new NotFoundException("Person with this email wasn't found!");
        return new ResponseEntity<>(person.get(), HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Person> updatePerson(@RequestHeader("Authorization") String token, @PathVariable("id") int id,
                                               @RequestBody @Valid Person person, BindingResult bindingResult) {

        person.setId(id);
        if (peopleService.findById(id).isEmpty())
            bindingResult.rejectValue("id", "", "Person with this id wasn't found!");

        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        if (found_person.get().getId() != id)
            throw new DataException("Attempt to change another person's data");

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        peopleService.update(person);

        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(HttpClientErrorException.Unauthorized e) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(e.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        // В HTTP ответе тело ответа (response) и в заголовке статус
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(NotFoundException e) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(e.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        // В HTTP ответе тело ответа (response) и в заголовке статус (404)
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(DataException e) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(e.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        // В HTTP ответе тело ответа (response) и в заголовке статус
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
