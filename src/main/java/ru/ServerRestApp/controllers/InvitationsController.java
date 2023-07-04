package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.repositories.TokensRepository;
import ru.ServerRestApp.models.*;
import ru.ServerRestApp.services.InvitationsService;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.services.TeamsService;
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
    private final PeopleService peopleService;
    private final InvitationValidator invitationValidator;
    private final TokensRepository tokensRepository;
    private final PersonUtil personUtil;
    @Autowired
    public InvitationsController(InvitationsService invitationsService, PeopleService peopleService, TeamsService teamsService, TeamsService teamsService1, InvitationValidator invitationValidator, TokensRepository tokensRepository, PersonUtil personUtil) {
        this.invitationsService = invitationsService;
        this.peopleService = peopleService;
        this.invitationValidator = invitationValidator;
        this.tokensRepository = tokensRepository;
        this.personUtil = personUtil;
    }

    /*
    // Получить все приглашения
    @GetMapping()
    public ResponseEntity<List<Invitation>> getAllInvitations() {
        List<Invitation> invitations = invitationsService.findAll();
        return new ResponseEntity<>(invitations, HttpStatus.OK);
    }
     */

    // Получить приглашения отправленные мной
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

    /*
    // Найти приглашение по id
    @GetMapping("/byId")
    public ResponseEntity<Invitation> getInvitation(@RequestBody Invitation bodyInvitation) {
        Optional<Invitation> invitation = invitationsService.findById(bodyInvitation.getId());
        if (invitation.isEmpty())
            throw new NotFoundException("Invitation with this id wasn't found!");
        return new ResponseEntity<>(invitation.get(), HttpStatus.OK);
    }
     */


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

    /*
    // Обновление приглашения
    @PostMapping("/update")
    public ResponseEntity<Invitation> updateInvitation(@RequestHeader("Authorization") String token,
                                                       @RequestBody @Valid Invitation invitation,
                                                       BindingResult bindingResult) {

        if (invitationsService.findById(invitation.getId()).isEmpty())
            bindingResult.rejectValue("id", "", "Invitation with this id wasn't found!");

        invitationValidator.validate(invitation, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        Person found_person = personUtil.getPersonByToken(token);

        if (found_person.getId() != invitation.getPersonFrom().getId())
            throw new DataException("Attempt to change another person's data");

        invitationsService.update(invitation);

        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }
     */

    @PostMapping("/delete")
    public ResponseEntity<Invitation> deleteInvitation(@RequestHeader("Authorization") String token,
                                                       @RequestBody Invitation bodyInvitation) {

        Optional<Invitation> foundInvitation = invitationsService.findById(bodyInvitation.getId());
        if (foundInvitation.isEmpty())
            throw new NotFoundException("Invitation with this id wasn't found!");

        Person found_person = personUtil.getPersonByToken(token);

        if (found_person.getId() != invitationsService.findById(bodyInvitation.getId()).get().getPersonFrom().getId() &&
                found_person.getId() != invitationsService.findById(bodyInvitation.getId()).get().getPersonTo().getId())
            throw new DataException("Attempt to change another person's data");

        invitationsService.delete(bodyInvitation.getId());

        return new ResponseEntity<>(foundInvitation.get(), HttpStatus.OK);
    }

    @PostMapping("/accept")
    public ResponseEntity<Invitation> acceptInvitation(@RequestHeader("Authorization") String token,
                                                       @RequestBody Invitation bodyInvitation) {

        Optional<Invitation> foundInvitation = invitationsService.findById(bodyInvitation.getId());
        if (foundInvitation.isEmpty())
            throw new NotFoundException("Invitation with this id wasn't found!");

        Person found_person = personUtil.getPersonByToken(token);

        if (found_person.getId() != invitationsService.findById(bodyInvitation.getId()).get().getPersonTo().getId())
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
