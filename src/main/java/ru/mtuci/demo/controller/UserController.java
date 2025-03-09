package ru.mtuci.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.demo.configuration.SecurityConfig;
import ru.mtuci.demo.model.ApplicationRole;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.repository.UserRepository;
import ru.mtuci.demo.request.DeleteUserRequest;
import ru.mtuci.demo.request.RegistrationRequest;
import ru.mtuci.demo.request.RequestUser;
import ru.mtuci.demo.service.impl.UserServiceImpl;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final SecurityConfig securityConfig;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/showAlladm")
    public ResponseEntity<?> showAlladm() {

        try {

            List<ApplicationUser> applicationUsers = userService.getAll();

            return ResponseEntity.status(HttpStatus.OK).body(applicationUsers);

        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Технические шоколадки...");

        }

    }

    @GetMapping("/showAll")
    public ResponseEntity<?> showAll() {

        try {

            List<ApplicationUser> applicationUsers = userService.getAll();

            List<RequestUser> requestUsers = applicationUsers.stream().map(
                    applicationUser -> new RequestUser(
                            applicationUser.getId(),
                            applicationUser.getUsername(),
                            applicationUser.getEmail(),
                            applicationUser.getRole()
                    )
            ).toList();

            return ResponseEntity.status(HttpStatus.OK).body(requestUsers);

        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Технические шоколадки...");

        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createadm")
    public ResponseEntity<?> createadm(@RequestBody RegistrationRequest registrationRequest) {

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
            applicationUser.setPassword(bCryptPasswordEncoder.encode(registrationRequest.getPassword()));
            applicationUser.setEmail(registrationRequest.getEmail());
            applicationUser.setRole(ApplicationRole.USER);

            userRepository.save(applicationUser);

            return ResponseEntity.status(HttpStatus.OK).body("Пользователь создан!");

        } catch(Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Технические шоколадки...");

        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateadm")
    public ResponseEntity<?> updateadm(@RequestBody ApplicationUser applicationUser) {

        try {

            if (applicationUser.getId() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите id пользователя!");

            if (userRepository.findById(applicationUser.getId()).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден!");

            ApplicationUser applicationUser1 = userRepository.findById(applicationUser.getId()).get();

            if (applicationUser.getUsername() == null) applicationUser.setUsername(applicationUser1.getUsername());

            if (applicationUser.getPassword() == null) applicationUser.setPassword(applicationUser1.getPassword());

            else applicationUser.setPassword(securityConfig.passwordEncoder().encode(applicationUser.getPassword()));

            if (applicationUser.getEmail() == null) applicationUser.setEmail(applicationUser1.getEmail());

            if (applicationUser.getRole() == null) applicationUser.setRole(applicationUser1.getRole());

            userRepository.save(applicationUser);

            return ResponseEntity.status(HttpStatus.OK).body("Пользователь успешно изменен!");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Технические шоколадки...");

        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteadm")
    public ResponseEntity<?> deleteadm(@RequestBody DeleteUserRequest deleteUserRequest) {

        try {

            if (deleteUserRequest.getId() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите id пользователя!");

            if (userRepository.findById(deleteUserRequest.getId()).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден!");

            userRepository.delete(userRepository.findById(deleteUserRequest.getId()).get());

            return ResponseEntity.status(HttpStatus.OK).body("Пользователь успешно удален!");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Технические шоколадки...");

        }

    }

}