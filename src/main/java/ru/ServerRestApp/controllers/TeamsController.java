package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.PersonTransaction;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.services.TeamsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.NotCreatedException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teams")
public class TeamsController {

    private final TeamsService teamsService;
    @Autowired
    public TeamsController(TeamsService teamsService) {
        this.teamsService = teamsService;
    }


    @GetMapping()
    public List<Team> getAllTeams() {
        return teamsService.findAll();
    }

    @GetMapping("/{id}")
    public Team getTeam(@PathVariable("id") int id) {
        Optional<Team> team = teamsService.findById(id);
        if (team.isPresent())
            return team.get();
        else
            throw new NotFoundException("Team with this id wasn't found!");
    }

    @PostMapping("/add")
    public ResponseEntity<Team> addTeam(@RequestBody @Valid Team team, BindingResult bindingResult) {

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

        teamsService.save(team);

        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @PostMapping("/update")
    public Team updateTeam(@RequestBody Team team) {
        teamsService.update(team);
        return teamsService.findById(team.getId()).get();
    }

    @PostMapping("/delete/{id}")
    public void deleteTeam(@PathVariable("id") int id) {
        teamsService.delete(id);
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
