package ru.ServerRestApp.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.services.PeopleService;

import java.util.Optional;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Person person = (Person)target;

        Optional<Person> found_person = peopleService.findByEmail(person.getEmail());
        if (found_person.isPresent() && found_person.get().getId() != person.getId())
            errors.rejectValue("email", "", "Person with this email already exists!");
    }
}
