package ru.ServerRestApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.services.TeamsService;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamsController {

    private final TeamsService teamsService;
    @Autowired
    public TeamsController(TeamsService teamsService) {
        this.teamsService = teamsService;
    }

    @GetMapping()
    public List<Team> getAllTeams() {
        return teamsService.findAll();
    }

    @GetMapping("/{id}")
    public Team getTeam(@PathVariable("id") int id) {
        return teamsService.findById(id);
    }

    @PostMapping("/add")
    public Team addTeam(@RequestBody Team team) {
        teamsService.save(team);
        return team;
    }

    @PostMapping("/update")
    public Team updateTeam(@RequestBody Team team) {
        teamsService.update(team);
        return teamsService.findById(team.getId());
    }

    @PostMapping("/delete/{id}")
    public void deleteTeam(@PathVariable("id") int id) {
        teamsService.delete(id);
    }
}
