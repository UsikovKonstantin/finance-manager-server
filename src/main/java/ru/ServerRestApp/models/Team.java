package ru.ServerRestApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "Team")
public class Team {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Название группы не должно быть пустым")
    @Size(min = 1, max = 50, message = "Название группы должно быть от 1 до 50 символов длиной")
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Person> people;
}
