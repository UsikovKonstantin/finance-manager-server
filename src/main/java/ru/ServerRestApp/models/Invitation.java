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

    public Invitation(Person personFrom, Person personTo) {
        this.personFrom = personFrom;
        this.personTo = personTo;
    }

    public Invitation(int id, Person personFrom, Person personTo) {
        this.id = id;
        this.personFrom = personFrom;
        this.personTo = personTo;
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

    @Override
    public String toString() {
        return "Invitation{" +
                "id=" + id +
                ", personFrom=" + personFrom +
                ", personTo=" + personTo +
                '}';
    }
}