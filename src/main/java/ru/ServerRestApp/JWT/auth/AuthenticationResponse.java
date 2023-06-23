package ru.ServerRestApp.JWT.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private final String type = "Bearer";
    private  String token;
    private String refreshToken;
}
