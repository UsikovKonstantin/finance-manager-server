package ru.ServerRestApp.JWT.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ServerRestApp.models.Team;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private Team team;
    private String name;
    private String Email;
    private String password;
    private double balance;
    private String gender;
    private String role;
}
