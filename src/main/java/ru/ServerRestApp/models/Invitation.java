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
    private Person personFrom;

    @ManyToOne
    @JoinColumn(name = "person_to_id", referencedColumnName = "id")
    private Person personTo;


    public Invitation() {
    }

    public Invitation(Person person_from, Person person_to) {
        this.personFrom = person_from;
        this.personTo = person_to;
    }

    public Invitation(int id, Person person_from, Person person_to) {
        this.id = id;
        this.personFrom = person_from;
        this.personTo = person_to;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person getPersonFrom() {
        return personFrom;
    }

    public void setPersonFrom(Person personFrom) {
        this.personFrom = personFrom;
    }

    public Person getPersonTo() {
        return personTo;
    }

    public void setPersonTo(Person personTo) {
        this.personTo = personTo;
    }
}
