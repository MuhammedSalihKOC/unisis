package edu.estu.unisis.controller;

import edu.estu.unisis.model.Department;
import edu.estu.unisis.model.User;
import edu.estu.unisis.service.DepartmentService;
import edu.estu.unisis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;
    private final DepartmentService departmentService;

    @Autowired
    public AdminController(UserService userService, DepartmentService departmentService) {
        this.userService = userService;
        this.departmentService = departmentService;
    }

    @GetMapping("/{rolePath}")
    public String showUsersByRole(
            @PathVariable String rolePath,
            @RequestParam(name = "sort", required = false, defaultValue = "name") String sortField,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") String sortDir,
            Model model) {

        List<User> users;
        String pageTitle = "";
        String roleLabel = "";
        String role = "";

        switch (rolePath.toLowerCase()) {
            case "ogrenciler":
                users = userService.getAllStudentsSorted(sortField, sortDir);
                pageTitle = "Öğrenciler";
                roleLabel = "öğrenci";
                role = "student";
                break;
            case "egitmenler":
                users = userService.getAllInstructorsSorted(sortField, sortDir);
                pageTitle = "Eğitmenler";
                roleLabel = "eğitmen";
                role = "instructor";
                break;
            case "yoneticiler":
                users = userService.getAllAdminsSorted(sortField, sortDir);
                pageTitle = "Yöneticiler";
                roleLabel = "yönetici";
                role = "admin";
                break;
            default:
                return "error/404";
        }

        model.addAttribute("users", users);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("roleLabel", roleLabel);
        model.addAttribute("role", role);
        model.addAttribute("rolePath", rolePath);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "user-list";
    }


    @GetMapping("/{rolePath}/{id}")
    public String showUserEditForm(@PathVariable String rolePath, @PathVariable Long id, Model model) {
        User user = userService.getById(id);
        List<Department> departments = departmentService.getAllDepartments();
        String pageTitle = "";
        String roleLabel = "";
        String role = "";

        switch (rolePath.toLowerCase()) {
            case "ogrenciler":
                pageTitle = "Öğrenci";
                roleLabel = "öğrenci";
                role = "student";
                break;
            case "egitmenler":
                pageTitle = "Eğitmen";
                roleLabel = "eğitmen";
                role = "instructor";
                break;
            case "yoneticiler":
                pageTitle = "Yönetici";
                roleLabel = "yönetici";
                role = "admin";
                break;
            default:
                return "error/404";
        }

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("roleLabel", roleLabel);
        model.addAttribute("role", role);
        model.addAttribute("rolePath", rolePath);
        model.addAttribute("departments", departments);

        return "user-edit";
    }
    @PostMapping("/{rolePath}/{id}/duzenle")
    public String updateUser(
            @PathVariable String rolePath,
            @PathVariable Long id,
            @ModelAttribute("user") User user,
            @RequestParam(value = "receipt", required = false) MultipartFile receipt,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (receipt != null && !receipt.isEmpty()) {
                user.setReceipt(receipt.getBytes());
            }
            Department department = departmentService.getDepartmentById(user.getDepartment().getId());
            user.setDepartment(department);

            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("message", "Kullanıcı başarıyla güncellendi.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Bir hata oluştu!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            e.printStackTrace();
        }
        return "redirect:/" + rolePath;
    }
    @PostMapping("/{rolePath}/{id}/sil")
    public String deleteUser(@PathVariable String rolePath,
                             @PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        System.out.println("Silme endpoint'i çağrıldı! id=" + id);
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Kullanıcı başarıyla silindi.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Kullanıcı silinemedi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/" + rolePath;
    }
}
