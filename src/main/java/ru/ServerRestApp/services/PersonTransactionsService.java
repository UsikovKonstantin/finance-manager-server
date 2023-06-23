package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.CategoryTransaction;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.PersonTransaction;
import ru.ServerRestApp.repositories.PeopleRepository;
import ru.ServerRestApp.repositories.PersonTransactionsRepository;
import ru.ServerRestApp.util.DataException;

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

        if (personTransaction.getPersonFrom().getBalance() - personTransaction.getAmount() < 0)
            throw new DataException("Balance must be positive!");
        personTransaction.getPersonFrom().setBalance(personTransaction.getPersonFrom().getBalance() - personTransaction.getAmount());
        personTransaction.getPersonTo().setBalance(personTransaction.getPersonTo().getBalance() + personTransaction.getAmount());

        int id = personTransactionsRepository.save(personTransaction).getId();
        personTransaction.setId(id);
    }

    @Transactional
    public void update(PersonTransaction personTransaction) {
        if (personTransaction.getPersonFrom() != null)
            personTransaction.setPersonFrom(peopleRepository.findById(personTransaction.getPersonFrom().getId()).get());
        if (personTransaction.getPersonTo() != null)
            personTransaction.setPersonTo(peopleRepository.findById(personTransaction.getPersonTo().getId()).get());

        PersonTransaction foundPersonTransaction = personTransactionsRepository.findById(personTransaction.getId()).get();
        if (foundPersonTransaction.getAmount() < personTransaction.getAmount()) {
            if (personTransaction.getPersonFrom().getBalance() + foundPersonTransaction.getAmount() - personTransaction.getAmount() < 0)
                throw new DataException("Balance must be positive!");
        }
        else {
            if (personTransaction.getPersonTo().getBalance() - foundPersonTransaction.getAmount() + personTransaction.getAmount() < 0)
                throw new DataException("Balance must be positive!");
        }
        personTransaction.getPersonFrom().setBalance(personTransaction.getPersonFrom().getBalance() + foundPersonTransaction.getAmount() - personTransaction.getAmount());
        personTransaction.getPersonTo().setBalance(personTransaction.getPersonTo().getBalance() - foundPersonTransaction.getAmount() + personTransaction.getAmount());


        int id = personTransactionsRepository.save(personTransaction).getId();
        personTransaction.setId(id);
    }

    @Transactional
    public void delete(int id) {
        PersonTransaction personTransaction = personTransactionsRepository.findById(id).get();
        if (personTransaction.getPersonFrom() != null)
            personTransaction.setPersonFrom(peopleRepository.findById(personTransaction.getPersonFrom().getId()).get());
        if (personTransaction.getPersonTo() != null)
            personTransaction.setPersonTo(peopleRepository.findById(personTransaction.getPersonTo().getId()).get());

        if (personTransaction.getPersonTo().getBalance() - personTransaction.getAmount() < 0)
            throw new DataException("Balance must be positive!");
        personTransaction.getPersonFrom().setBalance(personTransaction.getPersonFrom().getBalance() + personTransaction.getAmount());
        personTransaction.getPersonTo().setBalance(personTransaction.getPersonTo().getBalance() - personTransaction.getAmount());

        personTransactionsRepository.deleteById(id);
    }
}
