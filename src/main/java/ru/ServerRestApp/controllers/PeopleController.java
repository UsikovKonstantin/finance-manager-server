package ru.ServerRestApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/{id}")
    public List<Person> get(@PathVariable("id") int id) {
        return peopleService.findByTeamId(id);
    }
}
