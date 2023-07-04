package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.repositories.TokensRepository;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.models.Tokens;
import ru.ServerRestApp.repositories.PeopleRepository;
import ru.ServerRestApp.repositories.TeamsRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final TeamsRepository teamsRepository;
    private final TokensRepository tokensRepository;
    @Autowired
    public PeopleService(PeopleRepository peopleRepository, TeamsRepository teamsRepository, TokensRepository tokensRepository) {
        this.peopleRepository = peopleRepository;
        this.teamsRepository = teamsRepository;
        this.tokensRepository = tokensRepository;
    }


    @Transactional(readOnly = true)
    public Optional<Person> findById(int id) {
        return peopleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Person> findByEmail(String email) {
        return peopleRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Person> findByTeamId(int id) {
        return peopleRepository.findByTeamId(id);
    }

    @Transactional
    public void save(Person person) {
        if (person.getTeam() != null)
            person.setTeam(teamsRepository.findById(person.getTeam().getId()).get());

        int id = peopleRepository.save(person).getId();
        person.setId(id);
    }

    @Transactional
    public void update(Person person, boolean changePassword) {

        Person found_person = peopleRepository.findById(person.getId()).get();
        if (!found_person.getEmail().equals(person.getEmail())) {
            Optional<Tokens> tokens = tokensRepository.findByEmail(found_person.getEmail());
            if (tokens.isPresent()) {
                tokens.get().setEmail(person.getEmail());
                tokensRepository.save(tokens.get());
            }
        }
        found_person.setFull_name(person.getFull_name());
        found_person.setEmail(person.getEmail());
        found_person.setGender(person.getGender());
        if (changePassword)
            found_person.setPassword(person.getPassword());


        peopleRepository.save(found_person);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    @Transactional
    public void kick(int id) {

        Team team = new Team();
        team.setName("Новая группа");

        teamsRepository.save(team);

        Person person = peopleRepository.findById(id).get();
        person.setRole("ROLE_LEADER");
        person.setTeam(team);
        peopleRepository.save(person);
    }

    @Transactional
    public void makeLeader(int idFrom, int idTo) {

        Person from = peopleRepository.findById(idFrom).get();
        from.setRole("ROLE_USER");
        peopleRepository.save(from);

        Person to = peopleRepository.findById(idTo).get();
        to.setRole("ROLE_LEADER");
        peopleRepository.save(to);
    }
}