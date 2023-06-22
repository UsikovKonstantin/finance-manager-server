package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.PersonTransaction;
import ru.ServerRestApp.repositories.PeopleRepository;
import ru.ServerRestApp.repositories.PersonTransactionsRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PersonTransactionsService {

    private final PersonTransactionsRepository personTransactionsRepository;
    private final PeopleRepository peopleRepository;
    @Autowired
    public PersonTransactionsService(PersonTransactionsRepository personTransactionsRepository, PeopleRepository peopleRepository) {
        this.personTransactionsRepository = personTransactionsRepository;
        this.peopleRepository = peopleRepository;
    }


    @Transactional(readOnly = true)
    public Optional<PersonTransaction> findById(int id) {
        return personTransactionsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findAll() {
        return personTransactionsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByPersonFromId(int id) {
        return personTransactionsRepository.findByPersonFromId(id);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByPersonToId(int id) {
        return personTransactionsRepository.findByPersonToId(id);
    }

    @Transactional
    public void save(PersonTransaction personTransaction) {
        if (personTransaction.getPersonFrom() != null)
            personTransaction.setPersonFrom(peopleRepository.findById(personTransaction.getPersonFrom().getId()).get());
        if (personTransaction.getPersonTo() != null)
            personTransaction.setPersonTo(peopleRepository.findById(personTransaction.getPersonTo().getId()).get());

        int id = personTransactionsRepository.save(personTransaction).getId();
        personTransaction.setId(id);
    }

    @Transactional
    public void update(PersonTransaction personTransaction) {
        if (personTransaction.getPersonFrom() != null)
            personTransaction.setPersonFrom(peopleRepository.findById(personTransaction.getPersonFrom().getId()).get());
        if (personTransaction.getPersonTo() != null)
            personTransaction.setPersonTo(peopleRepository.findById(personTransaction.getPersonTo().getId()).get());

        int id = personTransactionsRepository.save(personTransaction).getId();
        personTransaction.setId(id);
    }

    @Transactional
    public void delete(int id) {
        personTransactionsRepository.deleteById(id);
    }
}
