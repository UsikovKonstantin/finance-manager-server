package ru.ServerRestApp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ServerRestApp.JWT.repository.TokensRepository;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.Tokens;
import ru.ServerRestApp.services.PeopleService;

import java.util.Optional;

@Component
public class PersonUtil {
    private final TokensRepository tokensRepository;
    private final PeopleService peopleService;

    public PersonUtil(TokensRepository tokensRepository, PeopleService peopleService) {
        this.tokensRepository = tokensRepository;
        this.peopleService = peopleService;
    }

    public Person getPersonByToken(String token) {

        Optional<Tokens> found_tokens = tokensRepository.findByAccessToken(token.substring(7));
        if (found_tokens.isEmpty())
            throw new NotFoundException("Token wasn't found!");

        Optional<Person> found_person = peopleService.findByEmail(found_tokens.get().getEmail());
        if (found_person.isEmpty())
            throw new NotFoundException("Person wasn't found!");

        return found_person.get();
    }
}
