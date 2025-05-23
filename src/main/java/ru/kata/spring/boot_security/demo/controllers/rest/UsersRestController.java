package ru.kata.spring.boot_security.demo.controllers.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.LoginRequest;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.dto.UserRegistrationDTO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.util.UserErrorResponse;
import ru.kata.spring.boot_security.demo.util.UserNotCreatedException;
import ru.kata.spring.boot_security.demo.util.UserNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // Добавляем CORS
public class UsersRestController {

    private final UserService userService;
    private final RoleService roleService;
    private final ModelMapper modelMapper;

    @Autowired
    public UsersRestController(UserService userService, RoleService roleService, ModelMapper modelMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
    }
    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> getCsrfToken(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        return ResponseEntity.ok(Map.of("token", token.getToken()));
    }
//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
//
//        Authentication authentication = authenticationMa
//
//    }

    @GetMapping("/user") //REST для вывода залогиненного пользователя.
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // Получаем полные данные пользователя из базы (чтобы быть уверенными в актуальности)
        User fullUser = userService.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("Current user not found in database"));
        // Конвертируем в DTO
        UserDTO userDTO = convertToUserDTO(fullUser);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers() {
        //return userService.findAll();
        List<UserDTO> userDTOs = userService.findAll().stream().map(this::convertToUserDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") int id) {
      //  return userService.findById(id);//Jackson конвертирует отданный объект в JSON
        Optional<User> optionalUser = userService.findById(id);

        if (optionalUser.isPresent()){
            UserDTO userDTO = convertToUserDTO(optionalUser.get());
            return ResponseEntity.ok(userDTO);
           // return userDTO;
        } else {
            // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
    }

    //ResponseEntity<> - может вернуть любой объект и Jackson его конвертнет в JSON
    //RequestBody - сконвертирует пришедший JSON в Java объект
    //мы принимаем DTO от клиента и здесь же конвертируем его в модель
    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
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
        // ModelMapper modelMapper = new ModelMapper();
        User user = modelMapper.map(userRegistrationDTO, User.class);

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

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

}
