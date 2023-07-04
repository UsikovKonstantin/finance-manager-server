package ru.ServerRestApp.JWT.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ServerRestApp.models.Person;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordResponse {
    private final String type = "Bearer";
    private  String token;
    private String error;
    private Person person;
}
