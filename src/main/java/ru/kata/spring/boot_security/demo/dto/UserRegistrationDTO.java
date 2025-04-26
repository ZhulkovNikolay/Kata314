package ru.kata.spring.boot_security.demo.dto;

import ru.kata.spring.boot_security.demo.models.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRegistrationDTO {

    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String username;

    @Email
    @NotEmpty(message = "Почтовый адрес не должен быть пустым")
    private String email;

    private String password;

    @NotEmpty(message = "Выберите хотя бы одну роль")
    private Set<Integer> roleIds; // нужны ИД ролей, так как из-за ссылок появляется циклическая зависимость.

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
