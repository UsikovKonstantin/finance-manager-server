package ru.ServerRestApp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.CategoryTransaction;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.services.InvitationsService;
import ru.ServerRestApp.util.ErrorResponse;
import ru.ServerRestApp.util.NotCreatedException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/invitations")
public class InvitationsController {

    private final InvitationsService invitationsService;
    @Autowired
    public InvitationsController(InvitationsService invitationsService) {
        this.invitationsService = invitationsService;
    }


    @GetMapping()
    public List<Invitation> getAllInvitations() {
        return invitationsService.findAll();
    }

    @GetMapping("/{id}")
    public Invitation getInvitation(@PathVariable("id") int id) {
        Optional<Invitation> invitation = invitationsService.findById(id);
        if (invitation.isPresent())
            return invitation.get();
        else
            throw new NotFoundException("Invitation with this id wasn't found!");
    }

    @PostMapping("/add")
    public ResponseEntity<Invitation> addInvitation(@RequestBody @Valid Invitation invitation, BindingResult bindingResult) {

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

        invitationsService.save(invitation);

        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    @PostMapping("/update")
    public Invitation updateInvitation(@RequestBody Invitation invitation) {
        invitationsService.update(invitation);
        return invitationsService.findById(invitation.getId()).get();
    }

    @PostMapping("/delete/{id}")
    public void deleteInvitation(@PathVariable("id") int id) {
        invitationsService.delete(id);
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
