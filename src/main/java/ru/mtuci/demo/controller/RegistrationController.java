package ru.mtuci.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.demo.model.ApplicationRole;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.repository.UserRepository;
import ru.mtuci.demo.request.RegistrationRequest;

@RestController
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRepository userRepository;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody RegistrationRequest registrationRequest) {

        try {

            if (registrationRequest.getUsername() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите логин!");

            if (registrationRequest.getEmail() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите почту!");

            if (registrationRequest.getPassword() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите пароль!");

            if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь с таким логином уже существует!");

            if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Данная почта уже используется!");

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            ApplicationUser applicationUser = new ApplicationUser();
            applicationUser.setUsername(registrationRequest.getUsername());
            applicationUser.setEmail(registrationRequest.getEmail());
            applicationUser.setPassword(bCryptPasswordEncoder.encode(registrationRequest.getPassword()));
            applicationUser.setRole(ApplicationRole.USER);

            userRepository.save(applicationUser);

            return ResponseEntity.status(HttpStatus.OK).body("Регистрация прошла успешно!");

        } catch(Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Технические шоколадки...");

        }

    }

}