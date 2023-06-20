package ru.ServerRestApp.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Invitation")
public class Invitation {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne
    @JoinColumn(name = "person_from_id", referencedColumnName = "id")
    private Person person_from;

    @ManyToOne
    @JoinColumn(name = "person_to_id", referencedColumnName = "id")
    private Person person_to;


    public Invitation() {
    }

    public Invitation(Person person_from, Person person_to) {
        this.person_from = person_from;
        this.person_to = person_to;
    }

    public Invitation(int id, Person person_from, Person person_to) {
        this.id = id;
        this.person_from = person_from;
        this.person_to = person_to;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person getPerson_from() {
        return person_from;
    }

    public void setPerson_from(Person person_from) {
        this.person_from = person_from;
    }

    public Person getPerson_to() {
        return person_to;
    }

    public void setPerson_to(Person person_to) {
        this.person_to = person_to;
    }
}
