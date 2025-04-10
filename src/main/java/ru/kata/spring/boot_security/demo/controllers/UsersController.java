package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.security.PersonDetails;

@Controller
public class UsersController {


    //попробуем получить данные аутетифицированного пользователя
    //из браузера по кукам достанется объект Principal
    @GetMapping("/showUserInfo")
    public String showUserInfo() {

        //Из потока текущего пользователя мы достаем Authentication
        //положен в Сессию, потом в контекс и из контиекст достал
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//из сессии достанется пользователь, помещен в поток, мы получим из потока
        //PersonDetails имплементирует UserDetails поэтому мы можем даункастить
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        System.out.println(personDetails.getPerson()); // мы вывели в консоль все данные о пользователе по адресу /showUserInfo
        return "user";
    }
}
