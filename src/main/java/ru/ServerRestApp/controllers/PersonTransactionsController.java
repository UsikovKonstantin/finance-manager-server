package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.PersonTransaction;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.services.PersonTransactionsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;

import static ru.ServerRestApp.util.ErrorsUtil.returnDataErrorsToClient;

@RestController
@RequestMapping("/personTransactions")
public class PersonTransactionsController {

    private final PersonTransactionsService personTransactionsService;
    private final PeopleService peopleService;
    @Autowired
    public PersonTransactionsController(PersonTransactionsService personTransactionsService, PeopleService peopleService) {
        this.personTransactionsService = personTransactionsService;
        this.peopleService = peopleService;
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
    public ResponseEntity<PersonTransaction> addPersonTransaction(@RequestBody @Valid PersonTransaction personTransaction, BindingResult bindingResult) {

        personTransaction.setId(0);
        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        if (personTransaction.getPersonFrom() == null)
            throw new DataException("Person_from must not be null!");

        Optional<Person> person_from = peopleService.findById(personTransaction.getPersonFrom().getId());
        if (person_from.isEmpty())
            throw new NotFoundException("Person_from with this id wasn't found!");

        if (personTransaction.getPersonTo() == null)
            throw new DataException("Person_to must not be null!");

        Optional<Person> person_to = peopleService.findById(personTransaction.getPersonTo().getId());
        if (person_to.isEmpty())
            throw new NotFoundException("Person_to with this id wasn't found!");

        if (personTransaction.getPersonFrom().getId() == personTransaction.getPersonTo().getId())
            throw new DataException("Person_from id must not be equal to Person_to id!");

        if (personTransaction.getCreated_at() == null)
            throw new DataException("Created_at must not be null!");

        personTransactionsService.save(personTransaction);

        return new ResponseEntity<>(personTransaction, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<PersonTransaction> updatePersonTransaction(@PathVariable("id") int id, @RequestBody @Valid PersonTransaction personTransaction, BindingResult bindingResult) {

        personTransaction.setId(id);
        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        Optional<PersonTransaction> foundPersonTransaction = personTransactionsService.findById(id);
        if (foundPersonTransaction.isEmpty())
            throw new NotFoundException("PersonTransaction with this id wasn't found!");

        if (personTransaction.getPersonFrom() == null)
            throw new DataException("Person_from must not be null!");

        Optional<Person> person_from = peopleService.findById(personTransaction.getPersonFrom().getId());
        if (person_from.isEmpty())
            throw new NotFoundException("Person_from with this id wasn't found!");

        if (personTransaction.getPersonTo() == null)
            throw new DataException("Person_to must not be null!");

        Optional<Person> person_to = peopleService.findById(personTransaction.getPersonTo().getId());
        if (person_to.isEmpty())
            throw new NotFoundException("Person_to with this id wasn't found!");

        if (personTransaction.getPersonFrom().getId() == personTransaction.getPersonTo().getId())
            throw new DataException("Person_from id must not be equal to Person_to id!");

        if (personTransaction.getCreated_at() == null)
            throw new DataException("Created_at must not be null!");

        personTransactionsService.update(personTransaction);

        return new ResponseEntity<>(personTransaction, HttpStatus.OK);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<PersonTransaction> deletePersonTransaction(@PathVariable("id") int id) {

        Optional<PersonTransaction> foundPersonTransaction = personTransactionsService.findById(id);
        if (foundPersonTransaction.isEmpty())
            throw new NotFoundException("PersonTransaction with this id wasn't found!");

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

}
