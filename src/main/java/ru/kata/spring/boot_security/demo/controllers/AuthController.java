package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.services.RegistrationService;
import ru.kata.spring.boot_security.demo.util.PersonValidator;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final PersonValidator personValidator;

    @Autowired
    public AuthController(RegistrationService registrationService, PersonValidator personValidator) {
        this.registrationService = registrationService;
        this.personValidator = personValidator;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("person") Person person) {
        return "auth/registration";
    }

    //Принимаем POST запрос
    //сюда придут данные с формы регистрации.
    //мы их получим с помощью @ModelAttribute("person")
    //Valid - чтобы работали аннотации над полями Персона @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        //дополнительно валидируем человека на предмет есть ли он в БД
        //передаем person с формы и в bindingResult помещается ошибка если она есть
        //человек с таким же именем не сможет зарегаться
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "/auth/registration";

        registrationService.register(person);
        //после успешной регистрации человек должен со своими зареганными данными аутентифицироваться
        return "redirect:/auth/login";
    }
}
