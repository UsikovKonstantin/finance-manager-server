package ru.ServerRestApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.PersonTransaction;
import ru.ServerRestApp.services.PersonTransactionsService;

import java.util.List;

@RestController
@RequestMapping("/personTransactions")
public class PersonTransactionsController {

    private final PersonTransactionsService personTransactionsService;
    @Autowired
    public PersonTransactionsController(PersonTransactionsService personTransactionsService) {
        this.personTransactionsService = personTransactionsService;
    }


    @GetMapping()
    public List<PersonTransaction> getAllPersonTransactions() {
        return personTransactionsService.findAll();
    }

    @GetMapping("/{id}")
    public PersonTransaction getPersonTransaction(@PathVariable("id") int id) {
        return personTransactionsService.findById(id);
    }

    @PostMapping("/add")
    public PersonTransaction addPersonTransaction(@RequestBody PersonTransaction personTransaction) {
        personTransactionsService.save(personTransaction);
        return personTransaction;
    }

    @PostMapping("/update")
    public PersonTransaction updatePersonTransaction(@RequestBody PersonTransaction personTransaction) {
        personTransactionsService.update(personTransaction);
        return personTransactionsService.findById(personTransaction.getId());
    }

    @PostMapping("/delete/{id}")
    public void deletePersonTransaction(@PathVariable("id") int id) {
        personTransactionsService.delete(id);
    }

}
