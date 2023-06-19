package ru.ServerRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.Person;

import java.util.Optional;

public interface PeopleRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmail(String email);
}
