package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.util.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //Set<Role> roles = new HashSet<>();
        //Role userRole = roleService.findOrCreateRole("USER");
        //user.setRoles(Set.of(userRole));
        user.setRoles(user.getRoles());
        enrichUser(user);
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUserById(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findById(int id) {
        Optional<User> foundUser = userRepository.findById(id);
        //return userRepository.findById(id);
        return foundUser;
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setRoles(user.getRoles());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        existingUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(existingUser);
    }

    private void enrichUser(User user) {

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        user.setCreatedWho(username);
        //user.setCreatedWho("ADMIN");
    }

}
