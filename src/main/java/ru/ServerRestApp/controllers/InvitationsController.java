package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.services.InvitationsService;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.services.TeamsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;

import static ru.ServerRestApp.util.ErrorsUtil.returnDataErrorsToClient;

@RestController
@RequestMapping("/invitations")
public class InvitationsController {

    private final InvitationsService invitationsService;
    private final PeopleService peopleService;
    @Autowired
    public InvitationsController(InvitationsService invitationsService, PeopleService peopleService, TeamsService teamsService, TeamsService teamsService1) {
        this.invitationsService = invitationsService;
        this.peopleService = peopleService;
    }


    @GetMapping()
    public ResponseEntity<List<Invitation>> getAllInvitations() {
        List<Invitation> invitations = invitationsService.findAll();
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

        invitation.setId(0);
        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        if (invitation.getPersonFrom() == null)
            throw new DataException("Person_from must not be null!");

        Optional<Person> person_from = peopleService.findById(invitation.getPersonFrom().getId());
        if (person_from.isEmpty())
            throw new NotFoundException("Person_from with this id wasn't found!");

        if (invitation.getPersonTo() == null)
            throw new DataException("Person_to must not be null!");

        Optional<Person> person_to = peopleService.findById(invitation.getPersonFrom().getId());
        if (person_to.isEmpty())
            throw new NotFoundException("Person_to with this id wasn't found!");

        if (invitation.getPersonFrom().getId() == invitation.getPersonTo().getId())
            throw new DataException("Person_from id must not be equal to Person_to id!");

        Optional<Invitation> foundInvitation = invitationsService.findByIdFromAndIdTo(
                invitation.getPersonFrom().getId(), invitation.getPersonTo().getId());
        if (foundInvitation.isPresent())
            throw new DataException("This invitation already exists!");

        if (peopleService.findById(invitation.getPersonFrom().getId()).get().getTeam().getId() ==
                peopleService.findById(invitation.getPersonTo().getId()).get().getTeam().getId())
            throw new DataException("These people are on the same team!");

        invitationsService.save(invitation);

        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Invitation> updateInvitation(@PathVariable("id") int id, @RequestBody @Valid Invitation invitation, BindingResult bindingResult) {

        invitation.setId(id);
        if (bindingResult.hasErrors())
            returnDataErrorsToClient(bindingResult);

        Optional<Invitation> found_invitation = invitationsService.findById(id);
        if (found_invitation.isEmpty())
            throw new NotFoundException("Invitation with this id wasn't found!");

        if (invitation.getPersonFrom() == null)
            throw new DataException("Person_from must not be null!");

        Optional<Person> person_from = peopleService.findById(invitation.getPersonFrom().getId());
        if (person_from.isEmpty())
            throw new NotFoundException("Person_from with this id wasn't found!");

        if (invitation.getPersonTo() == null)
            throw new DataException("Person_to must not be null!");

        Optional<Person> person_to = peopleService.findById(invitation.getPersonFrom().getId());
        if (person_to.isEmpty())
            throw new NotFoundException("Person_to with this id wasn't found!");

        if (invitation.getPersonFrom().getId() == invitation.getPersonTo().getId())
            throw new DataException("Person_from id must not be equal to Person_to id!");

        Optional<Invitation> foundInvitation = invitationsService.findByIdFromAndIdTo(
                invitation.getPersonFrom().getId(), invitation.getPersonTo().getId());
        if (foundInvitation.isPresent())
            throw new DataException("This invitation already exists!");

        if (invitation.getPersonFrom().getTeam().getId() == invitation.getPersonFrom().getTeam().getId())
            throw new DataException("These people are on the same team!");

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
