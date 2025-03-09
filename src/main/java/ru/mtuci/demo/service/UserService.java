package ru.mtuci.demo.service;

import ru.mtuci.demo.model.ApplicationUser;
import java.util.List;

public interface UserService {

    List<ApplicationUser> getAll();

}