package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.repository.UserRepository;
import ru.mtuci.demo.service.UserService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<ApplicationUser> getAll() {

        return userRepository.findAll();

    }

}