package ru.kata.spring.boot_security.demo.util;

public class RoleNotFoundExceprion extends RuntimeException{
    public RoleNotFoundExceprion(String message) {
        super(message);
    }
}
