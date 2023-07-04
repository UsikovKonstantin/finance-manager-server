package ru.ServerRestApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity
@Table(name = "Person_transaction")
public class PersonTransaction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Positive
    @Column(name = "amount")
    private double amount;

    @Column(name = "created_at")
    private Date createdAt;

    @Size(max = 50, message = "Описание транзакции должно быть до 50 символов длиной")
    @Column(name = "description")
    private String description;


    @ManyToOne
    @JoinColumn(name = "person_from_id", referencedColumnName = "id")
    private Person personFrom;

    @ManyToOne
    @JoinColumn(name = "person_to_id", referencedColumnName = "id")
    private Person personTo;


    public PersonTransaction() {
    }

    public PersonTransaction(double amount, Date created_at, String description, Person person_from, Person person_to) {
        this.amount = amount;
        this.createdAt = created_at;
        this.description = description;
        this.personFrom = person_from;
        this.personTo = person_to;
    }

    public PersonTransaction(int id, double amount, Date created_at, String description, Person person_from, Person person_to) {
        this.id = id;
        this.amount = amount;
        this.createdAt = created_at;
        this.description = description;
        this.personFrom = person_from;
        this.personTo = person_to;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "PersonTransaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", created_at=" + createdAt +
                ", description='" + description + '\'' +
                ", personFrom=" + personFrom +
                ", personTo=" + personTo +
                '}';
    }
}