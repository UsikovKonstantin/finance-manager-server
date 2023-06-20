package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.repositories.PeopleRepository;
import ru.ServerRestApp.repositories.TeamsRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final TeamsRepository teamsRepository;
    @Autowired
    public PeopleService(PeopleRepository peopleRepository, TeamsRepository teamsRepository) {
        this.peopleRepository = peopleRepository;
        this.teamsRepository = teamsRepository;
    }


    @Transactional(readOnly = true)
    public Person findById(int id) {
        Optional<Person> person = peopleRepository.findById(id);
        return person.get();
    }

    @Transactional(readOnly = true)
    public Person findByEmail(String email) {
        Optional<Person> person = peopleRepository.findByEmail(email);
        return person.get();
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
        peopleRepository.save(person);
    }

    @Transactional
    public void update(Person person) {
        if (person.getTeam() != null)
            person.setTeam(teamsRepository.findById(person.getTeam().getId()).get());
        peopleRepository.save(person);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

}
