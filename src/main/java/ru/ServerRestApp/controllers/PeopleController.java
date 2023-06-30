package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
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
    private final Validator validator;
    @Autowired
    public PeopleController(PeopleService peopleService, TeamsService teamsService, PasswordEncoder passwordEncoder, PersonValidator personValidator, TokensRepository tokensRepository, TokensRepository tokensRepository1, Validator validator) {
        this.peopleService = peopleService;
        this.teamsService = teamsService;
        this.passwordEncoder = passwordEncoder;
        this.personValidator = personValidator;
        this.tokensRepository = tokensRepository1;
        this.validator = validator;
    }

    @GetMapping()
    public ResponseEntity<List<Person>> getAllPeople() {
        List<Person> people = peopleService.findAll();
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    @GetMapping("/team/byId")
    public ResponseEntity<List<Person>> getPeopleByTeamId(@RequestBody Person bodyPerson) {
        Optional<Team> team = teamsService.findById(bodyPerson.getId());
        if (team.isEmpty())
            throw new NotFoundException("Team with this id wasn't found!");

        List<Person> people = peopleService.findByTeamId(bodyPerson.getId());
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    @GetMapping("/byId")
    public ResponseEntity<Person> getPerson(@RequestBody Person bodyPerson) {
        Optional<Person> person = peopleService.findById(bodyPerson.getId());
        if (person.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");
        return new ResponseEntity<>(person.get(), HttpStatus.OK);
    }

    @GetMapping("/email")
    public ResponseEntity<Person> getPersonByEmail(@RequestBody Person bodyPerson) {
        Optional<Person> person = peopleService.findByEmail(bodyPerson.getEmail());
        if (person.isEmpty())
            throw new NotFoundException("Person with this email wasn't found!");
        return new ResponseEntity<>(person.get(), HttpStatus.OK);
    }


    @PostMapping("/update")
    public ResponseEntity<Person> updatePerson(@RequestHeader("Authorization") String token,
                                               @RequestBody Person person) {

        BindingResult bindingResult = new BeanPropertyBindingResult(person, "person");
        person.setRole("ROLE_USER");
        boolean changePassword = true;
        if ("".equals(person.getPassword())) {
            changePassword = false;
            person.setPassword("***");
        }

        validator.validate(person, bindingResult);

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        person.setId(found_person.get().getId());
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        peopleService.update(person, changePassword);

        return new ResponseEntity<>(peopleService.findById(found_person.get().getId()).get(), HttpStatus.OK);
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
