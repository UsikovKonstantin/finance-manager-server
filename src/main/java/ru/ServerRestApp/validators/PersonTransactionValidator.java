package ru.ServerRestApp.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.PersonTransaction;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.services.PersonTransactionsService;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.Optional;

@Component
public class PersonTransactionValidator implements Validator {

    private final PersonTransactionsService personTransactionsService;
    private final PeopleService peopleService;

    @Autowired
    public PersonTransactionValidator(PersonTransactionsService personTransactionsService, PeopleService peopleService) {
        this.personTransactionsService = personTransactionsService;
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonTransaction.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        PersonTransaction personTransaction = (PersonTransaction)target;
        boolean from = false, to = false;

        if (personTransaction.getPersonFrom() == null)
            errors.rejectValue("personFrom", "", "PersonFrom must not be null!");
        else if (peopleService.findById(personTransaction.getPersonFrom().getId()).isEmpty())
            errors.rejectValue("personFrom", "", "Person with this id wasn't found!");
        else
            from = true;

        if (personTransaction.getPersonTo() == null)
            errors.rejectValue("personTo", "", "PersonTo must not be null!");
        else if (peopleService.findById(personTransaction.getPersonTo().getId()).isEmpty())
            errors.rejectValue("personTo", "", "Person with this id wasn't found!");
        else
            to = true;

        if (personTransaction.getCreated_at() == null)
            errors.rejectValue("created_at", "", "Created_at must not be null!");

        if (personTransaction.getAmount() == 0)
            errors.rejectValue("amount", "", "Amount must not be 0!");

        if (!from || !to) return;

        if (personTransaction.getPersonFrom().getId() == personTransaction.getPersonTo().getId())
            errors.rejectValue("personTo", "", "PersonTo id must not be equal to personFrom id!");
    }
}
