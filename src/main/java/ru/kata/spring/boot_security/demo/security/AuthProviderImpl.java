package ru.kata.spring.boot_security.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.services.PersonDetailsService;

import java.util.Collections;

@Component
public class AuthProviderImpl implements AuthenticationProvider {
    //мы будем сравнивать пароль и юзернейм введенный в форме с паролем в БД

    //внедряем наш кастомный сервис
    private final PersonDetailsService personDetailsService;

    @Autowired
    public AuthProviderImpl(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    //сюда кладем логин и пароль,
    //а возвращаем Principal - объект с данными о пользователе (PersonDetails)
    //пароль, юзернейм и данные об аккаунте, что он активен, не заблокирвоан и тп
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();

        //а у нас тут как раз реализован сервис поиска человека по имени
        //передаем ему юзернейм, пришедший с HTML формы
        UserDetails personDetails = personDetailsService.loadUserByUsername(username);
        String password = authentication.getCredentials().toString();

        //Благодаря тому, что мы используем обертку UserDetails, мы можем использовать одни и те же методы
        if (!password.equals(personDetails.getPassword()))
            throw new BadCredentialsException("Incorrect password");

        //возвращаем полноценный Principal, пароль и список прав
        //Этот класс реализует Authentication, поэтому мы можем его возвращать
        return new UsernamePasswordAuthenticationToken(personDetails, password, Collections.emptyList());
        //Этот объект будет помещен в Сессию и при каждом запросе пользователя из сессии будет доставаться
        //И мы будем иметь доступ к его PersonDetails
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //нужен чтобы понять для какого объекта Authentication работает данный провайдер
        //в случае когда у нас много провайдеров
        //т.к. у нас 1, вернем true
        return true;
    }


}
