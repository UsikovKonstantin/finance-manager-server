package ru.ServerRestApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.JWT.repository.TokensRepository;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.Tokens;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.NotFoundException;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/me")
public class MeController {

    private final TokensRepository tokensRepository;
    private final PeopleService peopleService;
    @Autowired
    public MeController(TokensRepository tokensRepository, PeopleService peopleService) {
        this.tokensRepository = tokensRepository;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public ResponseEntity<Person> getMe(@RequestBody Tokens tokens) {
        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(tokens.getAccessToken());
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        return new ResponseEntity<>(person.get(), HttpStatus.OK);
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
