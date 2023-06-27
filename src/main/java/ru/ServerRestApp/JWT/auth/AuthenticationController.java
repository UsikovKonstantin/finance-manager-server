package ru.ServerRestApp.JWT.auth;


import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;
    public Cookie cookie;
    @Autowired
    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, HttpServletResponse response){
        return ResponseEntity.ok(service.register(request, response));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody  AuthenticationRequest request, HttpServletResponse response){
        return ResponseEntity.ok(service.authenticate(request, response));
    }

    /*@PostMapping("/token")
    public ResponseEntity<AuthenticationResponse> getNewAccessToken(@RequestBody  RefreshJwtRequest  request){
        return ResponseEntity.ok(service.getAccessToken(request.getRefreshToken()));
    }*/

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> getNewRefreshToken(@CookieValue(value = "refreshToken", defaultValue = "")  String refreshToken, HttpServletResponse response) throws AuthException {
        return ResponseEntity.ok(service.refresh(refreshToken, response));
    }
}
