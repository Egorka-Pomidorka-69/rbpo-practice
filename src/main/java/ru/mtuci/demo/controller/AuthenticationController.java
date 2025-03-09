package ru.mtuci.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.demo.configuration.JwtTokenProvider;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.request.AuthenticationRequest;
import ru.mtuci.demo.model.AuthenticationResponse;
import ru.mtuci.demo.repository.UserRepository;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {

        try {

            String email = authenticationRequest.getEmail();

            if (email == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите почту!");

            if (authenticationRequest.getPassword() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите пароль!");

            Optional<ApplicationUser> applicationUser = userRepository.findByEmail(email);

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    email, authenticationRequest.getPassword()));

            String token = jwtTokenProvider.createToken(email, applicationUser.get().getRole().getGrantedAuthorities());

            return ResponseEntity.ok(new AuthenticationResponse(email, token));

        } catch (AuthenticationException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверная почта или пароль!");

        }

    }

}