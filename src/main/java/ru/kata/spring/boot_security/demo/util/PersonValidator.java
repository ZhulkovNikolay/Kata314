package ru.kata.spring.boot_security.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.services.PersonDetailsService;

@Component
public class PersonValidator implements Validator {

    private final PersonDetailsService personDetailsService;

    @Autowired
    public PersonValidator(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    //говорит нам о том, что этот класс нужен для объектов класса Person
    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    //В сервисе loadUserByUsername выбрасывает исключение. Мы опираемся на это исколючение
    //Так делать неочень красиво
    //TODO: 82 Желательно самостоятельно реализовать PeopleService с тем же методом и заменить personDetailsService
    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        try {
            personDetailsService.loadUserByUsername(person.getUsername());
        } catch (UsernameNotFoundException ignored) {
            return; //ловим исключение и игнорируем его. Если оно выброшено, значит все ОК. Пользователь с таким именем не найден
        }
        errors.rejectValue("username","","Человек с таким именем пользователя существует" );
    }
}
