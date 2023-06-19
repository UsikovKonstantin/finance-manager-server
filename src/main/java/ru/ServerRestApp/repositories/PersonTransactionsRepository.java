package ru.ServerRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.PersonTransaction;

public interface PersonTransactionsRepository extends JpaRepository<PersonTransaction, Integer> {
}
