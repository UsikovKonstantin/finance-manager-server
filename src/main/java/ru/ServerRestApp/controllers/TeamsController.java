package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.services.TeamsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;

import static ru.ServerRestApp.util.ErrorsUtil.returnDataErrorsToClient;

@RestController
@RequestMapping("/teams")
public class TeamsController {

    private final TeamsService teamsService;
    @Autowired
    public TeamsController(TeamsService teamsService) {
        this.teamsService = teamsService;
    }


    @CrossOrigin(origins = "http://127.0.0.1:5173")
    @GetMapping()
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamsService.findAll();
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://127.0.0.1:5173")
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeam(@PathVariable("id") int id) {
        Optional<Team> team = teamsService.findById(id);
        if (team.isEmpty())
            throw new NotFoundException("Team with this id wasn't found!");
        return new ResponseEntity<>(team.get(), HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://127.0.0.1:5173")
    @PostMapping("/add")
    public ResponseEntity<Team> addTeam(@RequestBody @Valid Team team, BindingResult bindingResult) {

        team.setId(0);
        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        teamsService.save(team);

        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://127.0.0.1:5173")
    @PostMapping("/update/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable("id") int id, @RequestBody @Valid Team team, BindingResult bindingResult) {

        team.setId(id);
        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        Optional<Team> foundTeam = teamsService.findById(id);
        if (foundTeam.isEmpty())
            throw new NotFoundException("Team with this id wasn't found!");

        teamsService.update(team);

        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://127.0.0.1:5173")
    @PostMapping("/delete/{id}")
    public ResponseEntity<Team> deleteTeam(@PathVariable("id") int id) {

        Optional<Team> foundTeam = teamsService.findById(id);
        if (foundTeam.isEmpty())
            throw new NotFoundException("Team with this id wasn't found!");

        teamsService.delete(id);

        return new ResponseEntity<>(foundTeam.get(), HttpStatus.OK);
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
