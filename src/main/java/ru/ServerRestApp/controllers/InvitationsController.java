package ru.ServerRestApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.CategoryTransaction;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.services.InvitationsService;

import java.util.List;

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
        return invitationsService.findById(id);
    }

    @PostMapping("/add")
    public Invitation addInvitation(@RequestBody Invitation invitation) {
        invitationsService.save(invitation);
        return invitation;
    }

    @PostMapping("/update")
    public Invitation updateInvitation(@RequestBody Invitation invitation) {
        invitationsService.update(invitation);
        return invitationsService.findById(invitation.getId());
    }

    @PostMapping("/delete/{id}")
    public void deleteInvitation(@PathVariable("id") int id) {
        invitationsService.delete(id);
    }

}
