package com.ecommerce.user.controller;

import com.ecommerce.user.dto.AuthRequest;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getFirstName(), authRequest.getPassword()));

        if (authenticate.isAuthenticated()) {
            return authService.generateToken(authRequest.getFirstName());
        } else {
            throw new RuntimeException("invalid access");
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        authService.validateToken(token);
        return "Token is valid";
    }
}

// without AuthenticationManager, anyone get generate token. But we want to generate token of
// only users saved in database. So create UserDetailsService bean who will connect to db and
// provide the user info to AuthenticationProvider. Then AuthenticationProvider will connect back to
// AuthenticationManager
