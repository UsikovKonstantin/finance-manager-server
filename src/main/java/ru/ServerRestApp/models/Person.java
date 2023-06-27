package ru.ServerRestApp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;
import jakarta.validation.constraints.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Person")
public class Person implements UserDetails {

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return null; }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

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

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", full_name='" + full_name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                ", gender='" + gender + '\'' +
                ", role='" + role + '\'' +
                ", team=" + team +
                '}';
    }
}
