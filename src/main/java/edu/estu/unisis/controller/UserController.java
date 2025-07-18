package edu.estu.unisis.controller;

import edu.estu.unisis.model.Department;
import edu.estu.unisis.model.User;
import edu.estu.unisis.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/giris")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping("/kayit")
    public String registerPage() {
        return "user/register";
    }

    @PostMapping("/user/login")
    public String login(@RequestParam("identifier") String identifier,
                        @RequestParam("password") String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        User user = userService.authenticate(identifier, password);

        if (user != null) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("message", "Giriş bilgileri hatalı. Lütfen tekrar deneyiniz.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/giris";
        }
    }

    @GetMapping("user/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "Oturumunuz sonlandırıldı.");
        redirectAttributes.addFlashAttribute("messageType", "warning");
        return "redirect:/giris";
    }
    @GetMapping("/profil")
    public String profilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("user", user);
        return "user/profile";
    }
}
