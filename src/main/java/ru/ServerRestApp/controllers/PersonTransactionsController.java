package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.PersonTransaction;
import ru.ServerRestApp.services.PersonTransactionsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.NotCreatedException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/personTransactions")
public class PersonTransactionsController {

    private final PersonTransactionsService personTransactionsService;
    @Autowired
    public PersonTransactionsController(PersonTransactionsService personTransactionsService) {
        this.personTransactionsService = personTransactionsService;
    }


    @GetMapping()
    public List<PersonTransaction> getAllPersonTransactions() {
        return personTransactionsService.findAll();
    }

    @GetMapping("/{id}")
    public PersonTransaction getPersonTransaction(@PathVariable("id") int id) {
        Optional<PersonTransaction> personTransaction = personTransactionsService.findById(id);
        if (personTransaction.isPresent())
            return personTransaction.get();
        else
            throw new NotFoundException("PersonTransaction with this id wasn't found!");
    }

    @PostMapping("/add")
    public ResponseEntity<PersonTransaction> addPersonTransaction(@RequestBody @Valid PersonTransaction personTransaction, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }

            throw new NotCreatedException(errorMsg.toString());
        }

        personTransactionsService.save(personTransaction);

        return new ResponseEntity<>(personTransaction, HttpStatus.OK);
    }

    @PostMapping("/update")
    public PersonTransaction updatePersonTransaction(@RequestBody PersonTransaction personTransaction) {
        personTransactionsService.update(personTransaction);
        return personTransactionsService.findById(personTransaction.getId()).get();
    }

    @PostMapping("/delete/{id}")
    public void deletePersonTransaction(@PathVariable("id") int id) {
        personTransactionsService.delete(id);
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
    private ResponseEntity<ErrorResponse> handleException(NotCreatedException e) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(e.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        // В HTTP ответе тело ответа (response) и в заголовке статус
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
