package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.services.InvitationsService;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.services.TeamsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;
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
    @Autowired
    public InvitationsController(InvitationsService invitationsService, PeopleService peopleService, TeamsService teamsService, TeamsService teamsService1, InvitationValidator invitationValidator) {
        this.invitationsService = invitationsService;
        this.peopleService = peopleService;
        this.invitationValidator = invitationValidator;
    }


    @GetMapping()
    public ResponseEntity<List<Invitation>> getAllInvitations() {
        List<Invitation> invitations = invitationsService.findAll();
        return new ResponseEntity<>(invitations, HttpStatus.OK);
    }

    @GetMapping("/personFrom/{id}")
    public ResponseEntity<List<Invitation>> getInvitationsByPersonFromId(@PathVariable("id") int id) {
        Optional<Person> person = peopleService.findById(id);
        if (person.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");

        List<Invitation> invitations = invitationsService.findByPersonFromId(id);
        return new ResponseEntity<>(invitations, HttpStatus.OK);
    }

    @GetMapping("/personTo/{id}")
    public ResponseEntity<List<Invitation>> getInvitationsByPersonToId(@PathVariable("id") int id) {
        Optional<Person> person = peopleService.findById(id);
        if (person.isEmpty())
            throw new NotFoundException("Person with this id wasn't found!");

        List<Invitation> invitations = invitationsService.findByPersonToId(id);
        return new ResponseEntity<>(invitations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invitation> getInvitation(@PathVariable("id") int id) {
        Optional<Invitation> invitation = invitationsService.findById(id);
        if (invitation.isEmpty())
            throw new NotFoundException("Invitation with this id wasn't found!");
        return new ResponseEntity<>(invitation.get(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Invitation> addInvitation(@RequestBody @Valid Invitation invitation, BindingResult bindingResult) {

        invitationValidator.validate(invitation, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        invitation.setId(0);
        invitationsService.save(invitation);

        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Invitation> updateInvitation(@PathVariable("id") int id, @RequestBody @Valid Invitation invitation, BindingResult bindingResult) {

        invitation.setId(id);

        if (invitationsService.findById(id).isEmpty())
            bindingResult.rejectValue("id", "", "Invitation with this id wasn't found!");

        invitationValidator.validate(invitation, bindingResult);

        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        invitationsService.update(invitation);

        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Invitation> deleteInvitation(@PathVariable("id") int id) {

        Optional<Invitation> foundInvitation = invitationsService.findById(id);
        if (foundInvitation.isEmpty())
            throw new NotFoundException("Invitation with this id wasn't found!");

        invitationsService.delete(id);

        return new ResponseEntity<>(foundInvitation.get(), HttpStatus.OK);
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<Invitation> acceptInvitation(@PathVariable("id") int id) {

        Optional<Invitation> foundInvitation = invitationsService.findById(id);
        if (foundInvitation.isEmpty())
            throw new NotFoundException("Invitation with this id wasn't found!");

        invitationsService.accept(id);

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

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(HttpClientErrorException.Unauthorized e) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(e.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        // В HTTP ответе тело ответа (response) и в заголовке статус
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

}
