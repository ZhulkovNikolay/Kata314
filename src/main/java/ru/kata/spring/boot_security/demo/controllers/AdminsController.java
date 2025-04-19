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

    //Удаление пользователя через Модальное окно
    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam("id") int id) {
        System.out.println("зашли в метод delete");
        userService.deleteUserById(id);

        System.out.println("отработал delete");
        return "redirect:/admin";
    }

    //Процесс регистрации нового пользователя
    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/admin#nav-new-user";
        }
        userService.register(user);
        return "redirect:/admin";
    }

    //Процесс редактирования выбранного из списка пользователя через Модальное окно.
    @PostMapping("/edit-user")
    public String performEdit(@ModelAttribute("user") @Valid User user,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/admin";
        }
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("success", "User updated!");
        return "redirect:/admin";
    }

}