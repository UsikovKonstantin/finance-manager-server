package ru.ServerRestApp.JWT2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.Person;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Person, Integer>
{
    Optional<Person> findByEmail(String email);
}
