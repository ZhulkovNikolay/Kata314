package ru.kata.spring.boot_security.demo.controllers.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.dto.UserRegistrationDTO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.util.UserErrorResponse;
import ru.kata.spring.boot_security.demo.util.UserNotCreatedException;
import ru.kata.spring.boot_security.demo.util.UserNotFoundException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UsersRestController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UsersRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/sayHello")
    public String sayHello() {
        return "Hello! This is REST!11";
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public Optional<User> getUser(@PathVariable("id") int id) {
        return userService.findById(id);//Jackson конвертирует отданный объект в JSON
    }

    //ResponseEntity<> - может вернуть любой объект и Jackson его конвертнет в JSON
    //RequestBody - сконвертирует пришедший JSON в Java объект
    //мы принимаем DTO от клиента и здесь же конвертируем его в модель
    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {
        System.out.println("зашли в метод create");

        if (bindingResult.hasErrors()) {
            System.out.println("зашли в bindingResult метода create");
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            //Данное выброшенное исключение мы ловим здесь же в контроллере
            //И (методом ниже) через ExceptionHandler отдаем клиенту JSON с ошибкой
            throw new UserNotCreatedException(errorMessage.toString());
        }
        //userService.register(user);
        userService.register(convertToUser(userRegistrationDTO));
        System.out.println("зарегали юзера сервисом");
        return ResponseEntity.ok(HttpStatus.OK); //возврат пустого ответа со статусом 200
    }

    //Эти методы возвращают JSON с сообщением об ошибке

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse response = new UserErrorResponse(
                "User with this ID was not found", System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(), System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 4xx
    }

    private User convertToUser(UserRegistrationDTO userRegistrationDTO) {
    //    System.out.println("зашли в convertToUser");
   //     ModelMapper modelMapper = new ModelMapper();
     //   System.out.println("создали объект modelMapper");
   //     User user = modelMapper.map(userRegistrationDTO, User.class);
    //    System.out.println("закончили конвертарцию через modelMapper");
        System.out.println("Зашли в convertToUser");
        User user = new User();

        user.setUsername(userRegistrationDTO.getUsername());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword(userRegistrationDTO.getPassword());

        Set<Role> roles = new HashSet<>();
        for (Integer roleId : userRegistrationDTO.getRoleIds()) {
            Role role = roleService.findById(roleId)
                    .orElseThrow(() -> new UserNotFoundException("Role not found with id: " + roleId));
            roles.add(role);
        }
        user.setRoles(roles);

        // user.setRoles(userRegistrationDTO.getRoleIds());

        return user;
    }


}
