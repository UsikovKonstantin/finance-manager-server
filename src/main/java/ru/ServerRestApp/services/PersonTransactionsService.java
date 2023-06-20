package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.PersonTransaction;
import ru.ServerRestApp.models.Team;
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
    public PersonTransaction findById(int id) {
        Optional<PersonTransaction> personTransaction = personTransactionsRepository.findById(id);
        return personTransaction.get();
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findAll() {
        return personTransactionsRepository.findAll();
    }

    @Transactional
    public void save(PersonTransaction personTransaction) {
        personTransactionsRepository.save(personTransaction);
    }

    @Transactional
    public void update(int id, PersonTransaction personTransaction) {
        personTransaction.setId(id);
        personTransactionsRepository.save(personTransaction);
    }

    @Transactional
    public void delete(int id) {
        personTransactionsRepository.deleteById(id);
    }
}
