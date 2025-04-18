package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    void register(User user);

    List<User> findAll();

    void deleteUserById(Integer id);

    Optional<User> findById(int id);

    void updateUser(User user);

}
