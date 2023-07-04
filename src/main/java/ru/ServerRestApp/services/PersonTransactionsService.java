package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public List<PersonTransaction> findByPersonId(int id) {
        return personTransactionsRepository.findByPersonId(id);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByPersonFromId(int id) {
        return personTransactionsRepository.findByPersonFromId(id);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByPersonToId(int id) {
        return personTransactionsRepository.findByPersonToId(id);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByTeamId(int id) {
        return personTransactionsRepository.findByTeamId(id);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByFromTeamId(int id) {
        return personTransactionsRepository.findByFromTeamId(id);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByToTeamId(int id) {
        return personTransactionsRepository.findByToTeamId(id);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByPersonIdForMonth(int id, int month, int year) {
        return personTransactionsRepository.findByPersonIdForMonth(id, month, year);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByFromPersonIdForMonth(int id, int month, int year) {
        return personTransactionsRepository.findByFromPersonIdForMonth(id, month, year);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByToPersonIdForMonth(int id, int month, int year) {
        return personTransactionsRepository.findByToPersonIdForMonth(id, month, year);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByTeamIdForMonth(int id, int month, int year) {
        return personTransactionsRepository.findByTeamIdForMonth(id, month, year);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByFromTeamIdForMonth(int id, int month, int year) {
        return personTransactionsRepository.findByFromTeamIdForMonth(id, month, year);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findByToTeamIdForMonth(int id, int month, int year) {
        return personTransactionsRepository.findByToTeamIdForMonth(id, month, year);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findNLastTransactionsPerson(int id, int n) {
        Pageable pageable = PageRequest.of(0, n);
        return personTransactionsRepository.findNLastTransactionsPerson(id, pageable);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findNLastTransactionsFromPerson(int id, int n) {
        Pageable pageable = PageRequest.of(0, n);
        return personTransactionsRepository.findNLastTransactionsFromPerson(id, pageable);
    }

    @Transactional(readOnly = true)
    public List<PersonTransaction> findNLastTransactionsToPerson(int id, int n) {
        Pageable pageable = PageRequest.of(0, n);
        return personTransactionsRepository.findNLastTransactionsToPerson(id, pageable);
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
