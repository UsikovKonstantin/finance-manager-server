package ru.ServerRestApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.services.PeopleService;

import java.util.List;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    @Autowired
    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }


    @GetMapping()
    public List<Person> getAllPeople() {
        return peopleService.findAll();
    }

    @GetMapping("/teamId/{id}")
    public List<Person> getPeopleByTeamId(@PathVariable("id") int id) {
        return peopleService.findByTeamId(id);
    }

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable("id") int id) {
        return peopleService.findById(id);
    }

    @GetMapping("/email/{email}")
    public Person getPersonByEmail(@PathVariable("email") String email) {
        return peopleService.findByEmail(email);
    }

    @PostMapping("/add")
    public Person addPerson(@RequestBody Person person) {
        peopleService.save(person);
        return person;
    }

    @PostMapping("/update")
    public Person updatePerson(@RequestBody Person person) {
        peopleService.update(person);
        return peopleService.findById(person.getId());
    }

    @PostMapping("/delete/{id}")
    public void deleteTeam(@PathVariable("id") int id) {
        peopleService.delete(id);
    }

}
