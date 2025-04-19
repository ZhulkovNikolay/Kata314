package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminsController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final RoleService roleService;

    @Autowired
    public AdminsController(UserService userService, UserValidator userValidator, RoleService roleService) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.roleService = roleService;
    }

    //Страница со всеми пользователями при первом переходе на /admin
    @GetMapping()
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll());
        return "allusers";
    }

 //   Удаление пользователя
    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam("id") int id) {
        System.out.println("зашли в метод delete");
            userService.deleteUserById(id);

        System.out.println("отработал delete");
        return "redirect:/admin";
    }



    //Собсна сам процесс регистрации нового пользователя
    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) return "allusers";
        userService.register(user);
        return "redirect:/admin";
    }

    //HTML форма для редактирования выбранного из списка пользователя.
    @GetMapping("/edit-user/{id}")
    public String editPage(@PathVariable int id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id"));

        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        return "edit-user";
    }

    //Собсна сам процесс редактирования выбранного из списка пользователя.
    @PostMapping("/edit-user/{id}")
    public String performEdit(@PathVariable int id, @ModelAttribute("user") @Valid User user,
                              BindingResult bindingResult,
                              Model model) {

        if (bindingResult.hasErrors()) { //при ошибке возвращаемся на страницу и сразу внедряем туда роли
            model.addAttribute("allRoles", roleService.findAll());
            return "edit-user";
        }

        user.setId(id); // Убедимся, что ID сохраняется
        userService.updateUser(user);
        return "redirect:/admin";
    }

}