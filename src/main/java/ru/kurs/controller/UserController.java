package ru.kurs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kurs.entity.Quarry;
import ru.kurs.entity.User;
import ru.kurs.service.QuarryService;
import ru.kurs.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private QuarryService quarryService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("quarries", quarryService.getAllQuarries());
        return "user-form";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute User user) {
        log.info("Creating new user with role: {}", user.getRole());
        try {
            log.info("User details - username: {}, email: {}, role: {}", 
                    user.getUserName(), user.getEmail(), user.getRole());
            userService.createUser(user);
            log.info("Successfully created user with role: {}", user.getRole());
            return "redirect:/users";
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("quarries", quarryService.getAllQuarries());
        return "user-form";
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute User user) {
        userService.updateUser(user);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
} 