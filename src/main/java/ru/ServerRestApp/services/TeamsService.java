package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.repositories.TeamsRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TeamsService {

    private final TeamsRepository teamsRepository;
    @Autowired
    public TeamsService(TeamsRepository teamsRepository) {
        this.teamsRepository = teamsRepository;
    }


    @Transactional(readOnly = true)
    public Team findById(int id) {
        Optional<Team> team = teamsRepository.findById(id);
        return team.get();
    }

    @Transactional(readOnly = true)
    public List<Team> findAll() {
        return teamsRepository.findAll();
    }

    @Transactional
    public void save(Team team) {
        teamsRepository.save(team);
    }

    @Transactional
    public void update(Team updatedTeam) {
        teamsRepository.save(updatedTeam);
    }

    @Transactional
    public void delete(int id) {
        teamsRepository.deleteById(id);
    }
}
