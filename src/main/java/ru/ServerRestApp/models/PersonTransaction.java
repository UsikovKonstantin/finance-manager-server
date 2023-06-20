package ru.ServerRestApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity
@Table(name = "Person_transaction")
public class PersonTransaction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Min(value = 0, message = "Сумма перевода должна быть положительной")
    @Column(name = "amount")
    private double amount;

    @Column(name = "created_at")
    private Date created_at;

    @Size(max = 50, message = "Описание транзакции должно быть до 50 символов длиной")
    @Column(name = "description")
    private String description;


    @ManyToOne
    @JoinColumn(name = "person_from_id", referencedColumnName = "id")
    private Person person_from;

    @ManyToOne
    @JoinColumn(name = "person_to_id", referencedColumnName = "id")
    private Person person_to;


    public PersonTransaction() {
    }

    public PersonTransaction(double amount, Date created_at, String description, Person person_from, Person person_to) {
        this.amount = amount;
        this.created_at = created_at;
        this.description = description;
        this.person_from = person_from;
        this.person_to = person_to;
    }

    public PersonTransaction(int id, double amount, Date created_at, String description, Person person_from, Person person_to) {
        this.id = id;
        this.amount = amount;
        this.created_at = created_at;
        this.description = description;
        this.person_from = person_from;
        this.person_to = person_to;
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

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
