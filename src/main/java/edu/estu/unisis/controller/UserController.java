package edu.estu.unisis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

    @GetMapping("/giris")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping("/kayit")
    public String registerPage() {
        return "user/register";
    }
}
