package ru.ServerRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.Invitation;

public interface InvitationsRepository extends JpaRepository<Invitation, Integer> {
}
