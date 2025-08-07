package edu.estu.unisis.controller;

import edu.estu.unisis.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {

    @GetMapping("/profil")
    public String profilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Profil");

        return "user/profile";
    }
}
