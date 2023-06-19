package ru.ServerRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.Team;

public interface TeamsRepository extends JpaRepository<Team, Integer> {
}
