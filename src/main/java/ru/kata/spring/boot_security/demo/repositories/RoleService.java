package ru.kata.spring.boot_security.demo.repositories;

import ru.kata.spring.boot_security.demo.models.Role;

public interface RoleService {
    //- class RegistrationService вырежи
    //- создай сервисы ролей и юзера с интерфейсами
    //- шифруй пароли юзерам в сервисе

    //Добавим содание ролей на будущее
    Role findOrCreateRole(String name);
}
