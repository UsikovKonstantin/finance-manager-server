package ru.ServerRestApp.controllers;

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
import ru.ServerRestApp.util.PersonUtil;
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
    private final PersonUtil personUtil;
    @Autowired
    public PeopleController(PeopleService peopleService, TeamsService teamsService, PasswordEncoder passwordEncoder, PersonValidator personValidator, TokensRepository tokensRepository, TokensRepository tokensRepository1, Validator validator, PersonUtil personUtil) {
        this.peopleService = peopleService;
        this.teamsService = teamsService;
        this.passwordEncoder = passwordEncoder;
        this.personValidator = personValidator;
        this.tokensRepository = tokensRepository1;
        this.validator = validator;
        this.personUtil = personUtil;
    }

    /*
    // Получить список всех людей
    @GetMapping()
    public ResponseEntity<List<Person>> getAllPeople() {
        List<Person> people = peopleService.findAll();
        return new ResponseEntity<>(people, HttpStatus.OK);
    }
     */

    // Получить список людей в группе
    @GetMapping("/team")
    public ResponseEntity<List<Person>> getPeopleByTeamId(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        List<Person> people = peopleService.findByTeamId(person.getTeam().getId());
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    // Получить себя
    @GetMapping("/me")
    public ResponseEntity<Person> getPerson(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);
        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    // Выгнать человека из группы
    @PostMapping("/kick")
    public ResponseEntity<Person> kick(@RequestHeader("Authorization") String token,
                                       @RequestBody Person personToKick) {

        Person person = personUtil.getPersonByToken(token);
        if (!"ROLE_LEADER".equals(person.getRole()))
            throw new DataException("Person who kicks should have the ROLE_LEADER!");

        Optional<Person> toKick = peopleService.findById(personToKick.getId());
        if (toKick.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");

        if (!"ROLE_USER".equals(toKick.get().getRole()))
            throw new DataException("Person being kicked should have the ROLE_USER!");

        if (person.getTeam().getId() != toKick.get().getTeam().getId())
            throw new DataException("People must be in the same team!");

        peopleService.kick(toKick.get().getId());

        return new ResponseEntity<>(peopleService.findById(toKick.get().getId()).get(), HttpStatus.OK);
    }

    // Сделать человека лидером
    @PostMapping("/giveLeader")
    public ResponseEntity<Person> giveLeader(@RequestHeader("Authorization") String token,
                                       @RequestBody Person personToBeLeader) {

        Person person = personUtil.getPersonByToken(token);
        if (!"ROLE_LEADER".equals(person.getRole()))
            throw new DataException("Person who gives a role should have the ROLE_LEADER!");

        Optional<Person> toLeader = peopleService.findById(personToBeLeader.getId());
        if (toLeader.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");

        if (!"ROLE_USER".equals(toLeader.get().getRole()))
            throw new DataException("Person who gets a role should have the ROLE_USER!");

        if (person.getTeam().getId() != toLeader.get().getTeam().getId())
            throw new DataException("People must be in the same team!");

        peopleService.makeLeader(person.getId(), toLeader.get().getId());

        return new ResponseEntity<>(peopleService.findById(toLeader.get().getId()).get(), HttpStatus.OK);
    }


    /*
    // Получить человека по email
    @GetMapping("/email")
    public ResponseEntity<Person> getPersonByEmail(@RequestBody Person bodyPerson) {
        Optional<Person> person = peopleService.findByEmail(bodyPerson.getEmail());
        if (person.isEmpty())
            throw new NotFoundException("Person with this email wasn't found!");
        return new ResponseEntity<>(person.get(), HttpStatus.OK);
    }
     */


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

        Person found_person = personUtil.getPersonByToken(token);

        person.setId(found_person.getId());
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        peopleService.update(person, changePassword);

        return new ResponseEntity<>(peopleService.findById(found_person.getId()).get(), HttpStatus.OK);
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
