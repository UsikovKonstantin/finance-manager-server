package ru.ServerRestApp.models;

import jakarta.persistence.*;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

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

/*
    @OneToMany(mappedBy = "team")
    private List<Person> people;
*/

    public Team() {
    }

    public Team(String name, List<Person> people) {
        this.name = name;
        //this.people = people;
    }

    public Team(int id, String name, List<Person> people) {
        this.id = id;
        this.name = name;
        //this.people = people;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
/*
    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }*/

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
