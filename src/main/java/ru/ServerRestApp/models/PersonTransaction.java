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
}
