package edu.estu.unisis.controller;

import edu.estu.unisis.model.Department;
import edu.estu.unisis.model.User;
import edu.estu.unisis.service.DepartmentService;
import edu.estu.unisis.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

@Controller
public class AuthController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, DepartmentService departmentService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/giris")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Giriş Yap");
        return "user/login";
    }

    @GetMapping("/kayit")
    public String registerPage(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        User user = new User();
        user.setDepartment(new Department());
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Okul Kaydı");
        return "user/register";
    }

    @PostMapping("user/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("school_number") String schoolNumber,
                               @RequestParam("password") String password,
                               @RequestParam("password_confirm") String passwordConfirm,
                               @RequestParam("number") String number,
                               @RequestParam("department") Long departmentId,
                               @RequestParam("receipt") MultipartFile receipt,
                               RedirectAttributes redirectAttributes) {

        if (!password.equals(passwordConfirm)) {
            redirectAttributes.addFlashAttribute("message", "Şifreler uyuşmuyor!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/kayit";
        }

        if (userService.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("message", "Bu e-posta zaten kullanılıyor.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/kayit";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setSchoolNumber(schoolNumber);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoneNumber(number);
        Department department = departmentService.getDepartmentById(departmentId);
        user.setDepartment(department);


        if (!receipt.isEmpty()) {
            try {
                byte[] fileBytes = receipt.getBytes();
                user.setReceipt(fileBytes);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", "Dosya yüklenemedi.");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                ;
                return "redirect:/kayit";
            }
        }

        userService.save(user);

        redirectAttributes.addFlashAttribute("message", "Başvurunuz başarıyla oluşturuldu. Kaydınızın tamamlanabilmesi için yetkili onayı gerekmektedir..");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/giris";

    }

    @PostMapping("/user/login")
    public String login(@RequestParam("identifier") String identifier,
                        @RequestParam("password") String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        User user = userService.authenticate(identifier, password);
        System.out.println("Rol nesnesi: " + user.getRole());
        System.out.println("Rol ismi: " + (user.getRole() != null ? user.getRole().getName() : "Role null"));
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
}
