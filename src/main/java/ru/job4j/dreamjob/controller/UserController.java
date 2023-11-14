package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ThreadSafe
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private User getUserFromHttpSession(HttpSession session) {
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        return user;
    }

    @GetMapping("/register")
    public String getRegistationPage(Model model, HttpSession session) {
        model.addAttribute("user", getUserFromHttpSession(session));
        return "users/register";
    }

    @PostMapping("/register")
    public String register(Model model, HttpSession session, @ModelAttribute User user) {
        model.addAttribute("user", getUserFromHttpSession(session));
        var savedUser = userService.save(user);
        if (savedUser.isEmpty()) {
            model.addAttribute("message", "Пользователь с такой почтой уже существует");
            return "errors/404";
        }
        return "users/login";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, HttpSession session) {
        model.addAttribute("user", getUserFromHttpSession(session));
        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest request) {
        var session = request.getSession();
        var userOptional = userService.findByEmailAndPassword(user.getEmail(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("user", getUserFromHttpSession(session));
            model.addAttribute("error", "Почта или пароль введены неверно");
            return "users/login";
        }
        session.setAttribute("user", userOptional.get());
        return "redirect:/vacancies";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }
}
