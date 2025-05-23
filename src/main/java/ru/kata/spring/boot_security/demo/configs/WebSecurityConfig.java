package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.kata.spring.boot_security.demo.services.UserDetailsServiceImpl;

import java.util.Arrays;

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
//                .sessionManagement()
//                .sessionFixation().newSession() // Принудительно создает новую сессию
//                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // Не кешировать
//                .and()
                .cors().configurationSource(corsConfigurationSource())
                .and() // Используем явную CORS конфигурацию
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringAntMatchers("/api/**") // Отключаем CSRF для API (для POSTMAN)
                .and()
                .csrf().disable()//временное отключение для POSTMAN
                .authorizeRequests()
                .antMatchers("/api/csrf").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/login", "error").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                //.anyRequest().hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
               //.httpBasic()//Чтобы работал Postman
               //.and()
                .formLogin().disable()// Форма для фронтенда
               // .loginPage("/login")
               // .successHandler(successUserHandler)
              //  .loginProcessingUrl("/process_login")
             //   .failureUrl("/login?error")
             //   .permitAll()
              //  .and()
                //ОТКЛЮЧИТЬ, ЕСЛИ ПОНАДОБИТСЯ ТЕСТ ЧЕРЕЗ HTTP
                // .httpBasic() // Basic Auth для Postman
                 .httpBasic().disable() // Basic Auth для Postman
               // .realmName("API Realm")
              //  .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)  // Важно: уничтожает сессию
                .deleteCookies("JSESSIONID")  // Удаляет сессионную куку

                //.clearAuthentication(true)     // Очищает аутентификацию
                .and()
                .sessionManagement()
               // .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionFixation().migrateSession()  // Важно: защита от фиксации сессии
                .maximumSessions(1)
                .expiredUrl("/login?expired"); // Перенаправление при истечении сессии
    }

/*
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll() // Разрешаем все запросы без аутентификации
                .and()
                .httpBasic().disable() // Отключаем базовую аутентификацию
                .formLogin().disable() // Отключаем форму входа
                .logout().disable(); // Отключаем обработку выхода
    }
*/

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(getPasswordEncoder());
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //----------------
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000", // Для фронтенда на React/Vue
                "http://127.0.0.1:3000", // Альтернативный localhost
                "http://localhost:8080"  // Для доступа из браузера
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // Важно для работы с куками
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}