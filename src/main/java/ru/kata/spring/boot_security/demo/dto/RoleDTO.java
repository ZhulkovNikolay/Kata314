package ru.kata.spring.boot_security.demo.dto;

public class RoleDTO {
    //нам не нужно передавать в JSON всю информацию о Роли
    //к примеру id отдавать необязательно
    //плюс нужно избавиться от рекурсии, так как роль завязана на юзере, а юзер на роли


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;


}
