package edu.estu.unisis.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/giris";
        }
        return "home";
    }
}
