package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.*;
import ru.ServerRestApp.services.InvitationsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;
import ru.ServerRestApp.util.PersonUtil;
import ru.ServerRestApp.validators.InvitationValidator;

import java.util.List;
import java.util.Optional;

import static ru.ServerRestApp.util.ErrorsUtil.returnDataErrorsToClient;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/invitations")
public class InvitationsController {

    private final InvitationsService invitationsService;
    private final InvitationValidator invitationValidator;
    private final PersonUtil personUtil;
    @Autowired
    public InvitationsController(InvitationsService invitationsService, InvitationValidator invitationValidator, PersonUtil personUtil) {
        this.invitationsService = invitationsService;
        this.invitationValidator = invitationValidator;
        this.personUtil = personUtil;
    }


    // Получить приглашения, отправленные мной
    @GetMapping("/fromMe")
    public ResponseEntity<List<Invitation>> getInvitationsByPersonFromId(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        List<Invitation> invitations = invitationsService.findByPersonFromId(person.getId());
        return new ResponseEntity<>(invitations, HttpStatus.OK);
    }

    // Получить приглашения, отправленные мне
    @GetMapping("/toMe")
    public ResponseEntity<List<Invitation>> getInvitationsByPersonToId(@RequestHeader("Authorization") String token) {

        Person person = personUtil.getPersonByToken(token);

        List<Invitation> invitations = invitationsService.findByPersonToId(person.getId());
        return new ResponseEntity<>(invitations, HttpStatus.OK);
    }

    // Добавить приглашение
    @PostMapping("/add")
    public ResponseEntity<Invitation> addInvitation(@RequestHeader("Authorization") String token,
                                                    @RequestBody @Valid Invitation invitation,
                                                    BindingResult bindingResult) {

        Person person = personUtil.getPersonByToken(token);
        invitation.setPersonFrom(person);

        invitationValidator.validate(invitation, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        invitation.setId(0);
        invitationsService.save(invitation);

        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    // Удалить (отклонить) приглашение
    @PostMapping("/delete")
    public ResponseEntity<Invitation> deleteInvitation(@RequestHeader("Authorization") String token,
                                                       @RequestBody Invitation bodyInvitation) {

        Optional<Invitation> foundInvitation = invitationsService.findById(bodyInvitation.getId());
        if (foundInvitation.isEmpty())
            throw new NotFoundException("Invitation with this id wasn't found!");

        Person found_person = personUtil.getPersonByToken(token);

        if (found_person.getId() != foundInvitation.get().getPersonFrom().getId() &&
                found_person.getId() != foundInvitation.get().getPersonTo().getId())
            throw new DataException("Attempt to change another person's data");

        invitationsService.delete(bodyInvitation.getId());

        return new ResponseEntity<>(foundInvitation.get(), HttpStatus.OK);
    }

    // Принять приглашение
    @PostMapping("/accept")
    public ResponseEntity<Invitation> acceptInvitation(@RequestHeader("Authorization") String token,
                                                       @RequestBody Invitation bodyInvitation) {

        Optional<Invitation> foundInvitation = invitationsService.findById(bodyInvitation.getId());
        if (foundInvitation.isEmpty())
            throw new NotFoundException("Invitation with this id wasn't found!");

        Person found_person = personUtil.getPersonByToken(token);

        if (found_person.getId() != foundInvitation.get().getPersonTo().getId())
            throw new DataException("Attempt to change another person's data");

        invitationsService.accept(bodyInvitation.getId());

        return new ResponseEntity<>(foundInvitation.get(), HttpStatus.OK);
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
