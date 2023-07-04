package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.PersonTransaction;
import ru.ServerRestApp.services.PersonTransactionsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;
import ru.ServerRestApp.util.PersonUtil;
import ru.ServerRestApp.validators.PersonTransactionValidator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ru.ServerRestApp.util.ErrorsUtil.returnDataErrorsToClient;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/personTransactions")
public class PersonTransactionsController {

    private final PersonTransactionsService personTransactionsService;
    private final PersonTransactionValidator personTransactionValidator;
    private final PersonUtil personUtil;
    @Autowired
    public PersonTransactionsController(PersonTransactionsService personTransactionsService, PersonTransactionValidator personTransactionValidator, PersonUtil personUtil) {
        this.personTransactionsService = personTransactionsService;
        this.personTransactionValidator = personTransactionValidator;
        this.personUtil = personUtil;
    }


    // Получить транзакции пользователя за все время
    @GetMapping("/person")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByPersonId(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByPersonId(person.getId());
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }

    // Получить исходящие транзакции пользователя за все время
    @GetMapping("/person/from")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByPersonFromId(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByPersonFromId(person.getId());
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }

    // Получить входящие транзакции пользователя за все время
    @GetMapping("/person/to")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByPersonToId(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByPersonToId(person.getId());
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }

    // Получить транзакции, входящие или исходящие от членов команды за все время
    @GetMapping("/team")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByTeamId(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByTeamId(person.getTeam().getId());
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }

    // Получить транзакции исходящие от членов команды за все время
    @GetMapping("/team/from")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsFromTeamId(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByFromTeamId(person.getTeam().getId());
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }

    // Получить транзакции входящие членам команды за все время
    @GetMapping("/team/to")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsToTeamId(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByToTeamId(person.getTeam().getId());
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }


    // Получить транзакцию по id
    @GetMapping("/byId")
    public ResponseEntity<PersonTransaction> getPersonTransaction(@RequestBody PersonTransaction bodyPersonTransaction) {
        Optional<PersonTransaction> personTransaction = personTransactionsService.findById(bodyPersonTransaction.getId());
        if (personTransaction.isEmpty())
            throw new NotFoundException("PersonTransaction with this id wasn't found!");
        return new ResponseEntity<>(personTransaction.get(), HttpStatus.OK);
    }



    // Получить транзакции пользователя за месяц
    @GetMapping("/person/month")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByPersonIdForMonth(@RequestHeader("Authorization") String token,
                                                                                               @RequestParam("timestamp") long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByPersonIdForMonth(
                person.getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }

    // Получить исходящие транзакции пользователя за месяц
    @GetMapping("/person/from/month")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByFromPersonIdForMonth(@RequestHeader("Authorization") String token,
                                                                                           @RequestParam("timestamp") long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByFromPersonIdForMonth(
                person.getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }

    // Получить входящие транзакции пользователя за месяц
    @GetMapping("/person/to/month")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByToPersonIdForMonth(@RequestHeader("Authorization") String token,
                                                                                           @RequestParam("timestamp") long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByToPersonIdForMonth(
                person.getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }



    // Получить транзакции группы за месяц
    @GetMapping("/team/month")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByTeamIdForMonth(@RequestHeader("Authorization") String token,
                                                                                           @RequestParam("timestamp") long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByTeamIdForMonth(
                person.getTeam().getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }

    // Получить исходящие транзакции группы за месяц
    @GetMapping("/team/from/month")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByFromTeamIdForMonth(@RequestHeader("Authorization") String token,
                                                                                         @RequestParam("timestamp") long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByFromTeamIdForMonth(
                person.getTeam().getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }

    // Получить входящие транзакции группы за месяц
    @GetMapping("/team/to/month")
    public ResponseEntity<List<PersonTransaction>> getPersonTransactionsByToTeamIdForMonth(@RequestHeader("Authorization") String token,
                                                                                             @RequestParam("timestamp") long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        Person person = personUtil.getPersonByToken(token);

        List<PersonTransaction> personTransactions = personTransactionsService.findByToTeamIdForMonth(
                person.getTeam().getId(), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        return new ResponseEntity<>(personTransactions, HttpStatus.OK);
    }


    // Получить n последних входящих или исходящих переводов пользователя
    @GetMapping("/person/last")
    public ResponseEntity<List<PersonTransaction>> findNLastTransactionsPerson(@RequestHeader("Authorization") String token,
                                                                                            @RequestParam("limit") int limit) {

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(personTransactionsService.findNLastTransactionsPerson(person.getId(), limit), HttpStatus.OK);
    }

    // Получить n последних исходящих переводов пользователя
    @GetMapping("/person/from/last")
    public ResponseEntity<List<PersonTransaction>> findNLastTransactionsFromPerson(@RequestHeader("Authorization") String token,
                                                                                          @RequestParam("limit") int limit) {

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(personTransactionsService.findNLastTransactionsFromPerson(person.getId(), limit), HttpStatus.OK);
    }

    // Получить n последних входящих переводов пользователя
    @GetMapping("/person/to/last")
    public ResponseEntity<List<PersonTransaction>> findNLastTransactionsToPerson(@RequestHeader("Authorization") String token,
                                                                                          @RequestParam("limit") int limit) {

        Person person = personUtil.getPersonByToken(token);

        return new ResponseEntity<>(personTransactionsService.findNLastTransactionsToPerson(person.getId(), limit), HttpStatus.OK);
    }


    // Добавить транзакцию
    @PostMapping("/add")
    public ResponseEntity<PersonTransaction> addPersonTransaction(@RequestHeader("Authorization") String token,
                                                                  @RequestBody @Valid PersonTransaction personTransaction,
                                                                  BindingResult bindingResult) {

        Person person = personUtil.getPersonByToken(token);

        personTransaction.setPersonFrom(person);

        personTransactionValidator.validate(personTransaction, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        personTransaction.setId(0);
        personTransactionsService.save(personTransaction);

        return new ResponseEntity<>(personTransaction, HttpStatus.OK);
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
