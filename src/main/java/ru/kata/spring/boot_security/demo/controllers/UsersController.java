package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.security.UserDetailsImpl;

@Controller
public class UsersController {

//    private final AdminService adminService;
//
//    @Autowired
//    public UsersController(AdminService adminService) {
//        this.adminService = adminService;
//    }

    //5. Пользователь с ролью user должен иметь доступ только к своей домашней странице /user, где выводятся его данные.
    // Доступ к этой странице должен быть только у пользователей с ролью user и admin.

    @GetMapping("/user")
    public String showUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println(userDetailsImpl.getUser());
        return "user";
    }

}
