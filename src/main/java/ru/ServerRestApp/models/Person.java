package ru.ServerRestApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name = "Person")
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Имя пользователя не должно быть пустым")
    @Size(min = 1, max = 50, message = "Имя пользователя должно быть от 1 до 50 символов длиной")
    @Column(name = "full_name")
    private String full_name;

    @NotEmpty(message = "Почтовый адрес не должен быть пустым")
    @Size(min = 1, max = 50, message = "Почтовый адрес должен быть от 1 до 50 символов длиной")
    @Email(message = "Неверный формат почтового адреса")
    @Column(name = "email")
    private String email;

    @NotEmpty(message = "Пароль не должен быть пустым")
    @Size(min = 1, max = 200, message = "Пароль должен быть от 1 до 200 символов длиной")
    @Column(name = "password")
    private String password;

    @Min(value = 0, message = "Баланс не может быть отрицательным")
    @Column(name = "balance")
    private double balance;

    @NotEmpty(message = "Пол не должен быть пустым")
    @Pattern(regexp = "^[MF]$", message = "Пол должен иметь значение либо 'M', либо 'F'")
    private String gender;

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;

    @OneToMany(mappedBy = "person")
    private List<CategoryTransaction> categoryTransactions;

    @OneToMany(mappedBy = "person_from")
    private List<Invitation> invitations_from;

    @OneToMany(mappedBy = "person_to")
    private List<Invitation> invitations_to;

    @OneToMany(mappedBy = "person_from")
    private List<PersonTransaction> transactions_from;

    @OneToMany(mappedBy = "person_to")
    private List<PersonTransaction> transactions_to;
}
