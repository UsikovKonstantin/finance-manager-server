package ru.ServerRestApp.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.services.InvitationsService;
import ru.ServerRestApp.services.PeopleService;

@Component
public class InvitationValidator implements Validator {

    private final InvitationsService invitationsService;
    private final PeopleService peopleService;

    @Autowired
    public InvitationValidator(InvitationsService invitationsService, PeopleService peopleService) {
        this.invitationsService = invitationsService;
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Invitation.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Invitation invitation = (Invitation)target;
        boolean from = false, to = false;

        if (invitation.getPersonFrom() == null)
            errors.rejectValue("personFrom", "", "PersonFrom must not be null!");
        else if (peopleService.findById(invitation.getPersonFrom().getId()).isEmpty())
            errors.rejectValue("personFrom", "", "Person with this id wasn't found!");
        else
            from = true;

        if (invitation.getPersonTo() == null)
            errors.rejectValue("personTo", "", "PersonTo must not be null!");
        else if (peopleService.findById(invitation.getPersonTo().getId()).isEmpty())
            errors.rejectValue("personTo", "", "Person with this id wasn't found!");
        else
            to = true;

        if (!from || !to) return;

        if (invitation.getPersonFrom().getId() == invitation.getPersonTo().getId())
            errors.rejectValue("personTo", "", "PersonTo id must not be equal to personFrom id!");

        if (invitationsService.findByIdFromAndIdTo(invitation.getPersonFrom().getId(), invitation.getPersonTo().getId()).isPresent())
            errors.rejectValue("personTo", "", "This invitation already exists!");

        if (peopleService.findById(invitation.getPersonFrom().getId()).get().getTeam().getId() ==
                peopleService.findById(invitation.getPersonTo().getId()).get().getTeam().getId())
            errors.rejectValue("personTo", "", "These people are on the same team!");
    }
}
