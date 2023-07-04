package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.services.TeamsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;
import ru.ServerRestApp.util.PersonUtil;

import static ru.ServerRestApp.util.ErrorsUtil.returnDataErrorsToClient;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/teams")
public class TeamsController {

    private final TeamsService teamsService;
    private final PersonUtil personUtil;
    @Autowired
    public TeamsController(TeamsService teamsService, PersonUtil personUtil) {
        this.teamsService = teamsService;
        this.personUtil = personUtil;
    }


    // Получить мою группу
    @GetMapping("/my")
    public ResponseEntity<Team> getTeam(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);
        return new ResponseEntity<>(person.getTeam(), HttpStatus.OK);
    }

    // Обновить название группы
    @PostMapping("/update")
    public ResponseEntity<Team> updateTeam(@RequestHeader("Authorization") String token,
                                           @RequestBody @Valid Team team, BindingResult bindingResult) {

        Person person = personUtil.getPersonByToken(token);
        team.setId(person.getTeam().getId());

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        teamsService.update(team);

        return new ResponseEntity<>(team, HttpStatus.OK);
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
