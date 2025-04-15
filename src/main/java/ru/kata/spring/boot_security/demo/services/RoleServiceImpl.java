package ru.kata.spring.boot_security.demo.services;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.RoleService;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    //- class RegistrationService вырежи
    //- создай сервисы ролей и юзера с интерфейсами
    //- шифруй пароли юзерам в сервисе

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    //Добавим содание ролей на будущее
    @Override
    public Role findOrCreateRole(String name) {
        String roleName = name.startsWith("ROLE_") ? name : "ROLE_" + name;
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
