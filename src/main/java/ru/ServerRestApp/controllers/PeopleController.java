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
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.NotCreatedException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    @Autowired
    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }


    @GetMapping()
    public List<Person> getAllPeople() {
        return peopleService.findAll();
    }

    @GetMapping("/teamId/{id}")
    public List<Person> getPeopleByTeamId(@PathVariable("id") int id) {
        return peopleService.findByTeamId(id);
    }

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable("id") int id) {
        Optional<Person> person = peopleService.findById(id);
        if (person.isPresent())
            return person.get();
        else
            throw new NotFoundException("Person with this id wasn't found!");
    }

    @GetMapping("/email/{email}")
    public Person getPersonByEmail(@PathVariable("email") String email) {
        Optional<Person> person = peopleService.findByEmail(email);
        if (person.isPresent())
            return person.get();
        else
            throw new NotFoundException("Person with this email wasn't found!");
    }

    @PostMapping("/add")
    public ResponseEntity<Person> addPerson(@RequestBody @Valid Person person, BindingResult bindingResult) {

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

        peopleService.save(person);

        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @PostMapping("/update")
    public Person updatePerson(@RequestBody Person person) {
        peopleService.update(person);
        return peopleService.findById(person.getId()).get();
    }

    @PostMapping("/delete/{id}")
    public void deleteTeam(@PathVariable("id") int id) {
        peopleService.delete(id);
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
