package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.JWT.repository.TokensRepository;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.PersonTransaction;
import ru.ServerRestApp.models.Tokens;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.services.PersonTransactionsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;
import ru.ServerRestApp.validators.PersonTransactionValidator;
import ru.ServerRestApp.validators.PersonValidator;

import java.util.List;
import java.util.Optional;

import static ru.ServerRestApp.util.ErrorsUtil.returnDataErrorsToClient;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/personTransactions")
public class PersonTransactionsController {

    private final PersonTransactionsService personTransactionsService;
    private final PeopleService peopleService;
    private final PersonTransactionValidator personTransactionValidator;
    private final TokensRepository tokensRepository;
    @Autowired
    public PersonTransactionsController(PersonTransactionsService personTransactionsService, PeopleService peopleService, PersonValidator personValidator, PersonTransactionValidator personTransactionValidator, TokensRepository tokensRepository) {
        this.personTransactionsService = personTransactionsService;
        this.peopleService = peopleService;
        this.personTransactionValidator = personTransactionValidator;
        this.tokensRepository = tokensRepository;
    }


    @GetMapping()
    public ResponseEntity<List<PersonTransaction>> getAllPersonTransactions() {
        List<PersonTransaction> personTransaction = personTransactionsService.findAll();
        return new ResponseEntity<>(personTransaction, HttpStatus.OK);
    }

    @GetMapping("/personFrom/{id}")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByPersonFromId(@PathVariable("id") int id) {
        Optional<Person> person = peopleService.findById(id);
        if (person.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");

        List<PersonTransaction> personTransactions = personTransactionsService.findByPersonFromId(id);
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }

    @GetMapping("/personTo/{id}")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByPersonToId(@PathVariable("id") int id) {
        Optional<Person> person = peopleService.findById(id);
        if (person.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");

        List<PersonTransaction> personTransactions = personTransactionsService.findByPersonToId(id);
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonTransaction> getPersonTransaction(@PathVariable("id") int id) {
        Optional<PersonTransaction> personTransaction = personTransactionsService.findById(id);
        if (personTransaction.isEmpty())
            throw new NotFoundException("PersonTransaction with this id wasn't found!");
        return new ResponseEntity<>(personTransaction.get(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<PersonTransaction> addPersonTransaction(@RequestHeader("Authorization") String token,
                                                                  @RequestBody @Valid PersonTransaction personTransaction,
                                                                  BindingResult bindingResult) {

        personTransactionValidator.validate(personTransaction, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        if (found_person.get().getId() != personTransaction.getPersonFrom().getId())
            throw new DataException("Attempt to change another person's data");

        personTransaction.setId(0);
        personTransactionsService.save(personTransaction);

        return new ResponseEntity<>(personTransaction, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<PersonTransaction> updatePersonTransaction(@RequestHeader("Authorization") String token,
                                                                     @PathVariable("id") int id,
                                                                     @RequestBody @Valid PersonTransaction personTransaction,
                                                                     BindingResult bindingResult) {

        personTransaction.setId(id);
        if (personTransactionsService.findById(id).isEmpty())
            bindingResult.rejectValue("id", "", "PersonTransaction with this id wasn't found!");

        personTransactionValidator.validate(personTransaction, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        if (found_person.get().getId() != personTransaction.getPersonFrom().getId())
            throw new DataException("Attempt to change another person's data");

        personTransactionsService.update(personTransaction);

        return new ResponseEntity<>(personTransaction, HttpStatus.OK);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<PersonTransaction> deletePersonTransaction(@RequestHeader("Authorization") String token,
                                                                     @PathVariable("id") int id) {

        Optional<PersonTransaction> foundPersonTransaction = personTransactionsService.findById(id);
        if (foundPersonTransaction.isEmpty())
            throw new NotFoundException("PersonTransaction with this id wasn't found!");

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        if (found_person.get().getId() != personTransactionsService.findById(id).get().getPersonFrom().getId())
            throw new DataException("Attempt to change another person's data");

        personTransactionsService.delete(id);

        return new ResponseEntity<>(foundPersonTransaction.get(), HttpStatus.OK);
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

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(AuthenticationException e) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(e.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        // В HTTP ответе тело ответа (response) и в заголовке статус
        return new ResponseEntity<>(response, HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
    }

}
