package ru.ServerRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.PersonTransaction;

import java.util.List;

public interface PersonTransactionsRepository extends JpaRepository<PersonTransaction, Integer> {
    List<PersonTransaction> findByPersonFromId(int personFromId);
    List<PersonTransaction> findByPersonToId(int personFromId);
}
