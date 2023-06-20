package ru.ServerRestApp.models;

import javax.persistence.*;
import java.util.List;
import javax.validation.constraints.*;

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
    @Column(name = "gender")
    private String gender;

    @NotEmpty(message = "Роль не должна быть пустой")
    @Pattern(regexp = "^(ROLE_USER|ROLE_LEADER)$", message = "Роль должна иметь значение либо 'ROLE_USER', либо 'ROLE_LEADER'")
    @Column(name = "role")
    private String role;


    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;
/*
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
*/

    public Person() {
    }

    public Person(String full_name, String email, String password, double balance,
                  String gender, String role, Team team, List<CategoryTransaction> categoryTransactions,
                  List<Invitation> invitations_from, List<Invitation> invitations_to,
                  List<PersonTransaction> transactions_from, List<PersonTransaction> transactions_to) {
        this.full_name = full_name;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.gender = gender;
        this.role = role;
        this.team = team;
        //this.categoryTransactions = categoryTransactions;
        //this.invitations_from = invitations_from;
        //this.invitations_to = invitations_to;
        //this.transactions_from = transactions_from;
        //this.transactions_to = transactions_to;
    }

    public Person(int id, String full_name, String email, String password,
                  double balance, String gender, String role, Team team,
                  List<CategoryTransaction> categoryTransactions,
                  List<Invitation> invitations_from, List<Invitation> invitations_to,
                  List<PersonTransaction> transactions_from, List<PersonTransaction> transactions_to) {
        this.id = id;
        this.full_name = full_name;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.gender = gender;
        this.role = role;
        this.team = team;
        //this.categoryTransactions = categoryTransactions;
        //this.invitations_from = invitations_from;
        //this.invitations_to = invitations_to;
        //this.transactions_from = transactions_from;
        //this.transactions_to = transactions_to;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
/*
    public List<CategoryTransaction> getCategoryTransactions() {
        return categoryTransactions;
    }

    public void setCategoryTransactions(List<CategoryTransaction> categoryTransactions) {
        this.categoryTransactions = categoryTransactions;
    }

    public List<Invitation> getInvitations_from() {
        return invitations_from;
    }

    public void setInvitations_from(List<Invitation> invitations_from) {
        this.invitations_from = invitations_from;
    }

    public List<Invitation> getInvitations_to() {
        return invitations_to;
    }

    public void setInvitations_to(List<Invitation> invitations_to) {
        this.invitations_to = invitations_to;
    }

    public List<PersonTransaction> getTransactions_from() {
        return transactions_from;
    }

    public void setTransactions_from(List<PersonTransaction> transactions_from) {
        this.transactions_from = transactions_from;
    }

    public List<PersonTransaction> getTransactions_to() {
        return transactions_to;
    }

    public void setTransactions_to(List<PersonTransaction> transactions_to) {
        this.transactions_to = transactions_to;
    }*/
}
