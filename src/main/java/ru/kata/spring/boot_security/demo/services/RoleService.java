package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    Role findOrCreateRole(String name);

    List<Role> findAll();

    Optional<Role> findById(int id);
}
