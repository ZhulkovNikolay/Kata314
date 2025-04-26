package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.services.UserDetailsServiceImpl;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final SuccessUserHandler successUserHandler;

    @Autowired
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, SuccessUserHandler successUserHandler) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.successUserHandler = successUserHandler;
    }

    //TODO вернуть csrf
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()//временное отключение для POSTMAN
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/login", "error").permitAll()
                .anyRequest().hasAnyRole("USER", "ADMIN")
                .and()
                .httpBasic()//Чтобы работал Postman
                .and()
                .formLogin().loginPage("/login")
                .successHandler(successUserHandler)
                .loginProcessingUrl("/process_login")
                .failureUrl("/login?error")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login");
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(getPasswordEncoder());
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}