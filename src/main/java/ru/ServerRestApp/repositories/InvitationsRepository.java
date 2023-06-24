package ru.ServerRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Person;

import java.util.List;
import java.util.Optional;

public interface InvitationsRepository extends JpaRepository<Invitation, Integer> {
    Optional<Invitation> findByPersonFromIdAndPersonToId(int person_fromId, int person_toId);
    List<Invitation> findByPersonFromId(int personFromId);
    List<Invitation> findByPersonToId(int personToId);
    List<Invitation> findByPersonToIdAndPersonFromTeamId(int personToId, int personFromTeamId);
}
