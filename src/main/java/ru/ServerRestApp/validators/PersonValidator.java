package ru.ServerRestApp.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.PersonTransaction;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.services.PeopleService;
import ru.ServerRestApp.services.TeamsService;
import ru.ServerRestApp.util.DataException;
import ru.ServerRestApp.util.NotFoundException;

import java.util.Optional;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;
    private final TeamsService teamsService;

    @Autowired
    public PersonValidator(PeopleService peopleService, TeamsService teamsService) {
        this.peopleService = peopleService;
        this.teamsService = teamsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Person person = (Person)target;

        if (person.getTeam() == null)
            errors.rejectValue("team", "", "Team must not be null!");
        else if (teamsService.findById(person.getTeam().getId()).isEmpty())
            errors.rejectValue("team", "", "Team with this id wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(person.getEmail());
        if (found_person.isPresent() && found_person.get().getId() != person.getId())
            errors.rejectValue("email", "", "Person with this email already exists!");
    }
}
