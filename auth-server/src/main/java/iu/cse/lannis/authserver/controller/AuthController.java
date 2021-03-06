package iu.cse.lannis.authserver.controller;

import iu.cse.lannis.authserver.dto.UserSignInDto;
import iu.cse.lannis.authserver.dto.UserSignUpDto;
import iu.cse.lannis.authserver.entities.AuthResponse;
import iu.cse.lannis.authserver.entities.AuthSignInRequest;
import iu.cse.lannis.authserver.entities.AuthSignUpRequest;
import iu.cse.lannis.authserver.services.AuthService;
import iu.cse.lannis.authserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthSignInRequest authSignInRequest) {
        return ResponseEntity.ok(authService.login(authSignInRequest));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthSignUpRequest signUpRequest) {
        return ResponseEntity.ok(authService.register(signUpRequest));
    }
}
