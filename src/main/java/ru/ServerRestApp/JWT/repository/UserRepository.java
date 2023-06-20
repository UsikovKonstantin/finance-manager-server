package ru.ServerRestApp.JWT.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ServerRestApp.models.Person;

public interface UserRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByEmail(String email);

}
