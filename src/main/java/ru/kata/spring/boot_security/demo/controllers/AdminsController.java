package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.services.UserServiceImpl;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminsController {

    private final UserServiceImpl userServiceImpl;
    private final UserValidator userValidator;
    private final RoleServiceImpl roleServiceImpl;

    @Autowired
    public AdminsController(UserServiceImpl userServiceImpl, UserValidator userValidator, RoleServiceImpl roleServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.userValidator = userValidator;
        this.roleServiceImpl = roleServiceImpl;
    }

    //Страница со всеми пользователями при первом переходе на /admin
    @GetMapping()
    public String showAllUsers(Model model) {
        model.addAttribute("users", userServiceImpl.findAll());
        return "allusers";
    }

    //Возможность админу перейти на страницу выбранного пользователя из списка
    @GetMapping("/user/{id}")
    public String showUser(@PathVariable int id, Model model) {
        User user = userServiceImpl.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        return "user";
    }

    //Удаление пользователя
    @DeleteMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable int id) {
        userServiceImpl.deleteUserById(id);
        return "redirect:/admin";
    }

    //HTML форма для регистрации нового пользователя.
    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("user") User user) {
        return "registration";
    }

    //Собсна сам процесс регистрации нового пользователя
    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) return "registration";

        userServiceImpl.register(user);
        return "redirect:/login";
    }

    //HTML форма для редактирования выбранного из списка пользователя.
    @GetMapping("/edit-user/{id}")
    public String editPage(@PathVariable int id, Model model) {
        User user = userServiceImpl.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id"));

        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleServiceImpl.findAll());
        return "edit-user";
    }

    //Собсна сам процесс редактирования выбранного из списка пользователя.
    @PostMapping("/edit-user/{id}")
    public String performEdit(@PathVariable int id, @ModelAttribute("user") @Valid User user,
                              BindingResult bindingResult,
                              Model model) {

        if (bindingResult.hasErrors()) { //при ошибке возвращаемся на страницу и сразу внедряем туда роли
            model.addAttribute("allRoles", roleServiceImpl.findAll());
            return "edit-user";
        }

        user.setId(id); // Убедимся, что ID сохраняется
        userServiceImpl.updateUser(user);
        return "redirect:/admin";
    }

}