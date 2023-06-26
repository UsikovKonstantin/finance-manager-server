package ru.ServerRestApp.JWT.auth;


import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;
    public Cookie cookie;
    @Autowired
    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.register(request));
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody  AuthenticationRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/token")
    public ResponseEntity<AuthenticationResponse> getNewAccessToken(@RequestBody  RefreshJwtRequest  request){
        return ResponseEntity.ok(service.getAccessToken(request.getRefreshToken()));
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> getNewRefreshToken(@RequestBody  RefreshJwtRequest request) throws AuthException {
        return ResponseEntity.ok(service.refresh(request.getRefreshToken()));
    }
}
