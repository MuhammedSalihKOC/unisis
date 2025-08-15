package edu.estu.unisis.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(HttpSession session, Model model) {
        model.addAttribute("pageTitle", "Ana Sayfa");
        return "home/home";
    }
}
