package edu.estu.unisis.controller;

import edu.estu.unisis.model.*;
import edu.estu.unisis.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class AdminController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final RoleService roleService;
    private final CourseService courseService;
    private final SystemSettingService systemSettingService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public AdminController(UserService userService, DepartmentService departmentService, RoleService roleService, CourseService courseService, PasswordEncoder passwordEncoder, SystemSettingService systemSettingService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.roleService = roleService;
        this.courseService = courseService;
        this.passwordEncoder = passwordEncoder;
        this.systemSettingService = systemSettingService;
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

        return "user/user-list";
    }

    @GetMapping("/{rolePath}/{id}")
    public String showUserDetailForm(@PathVariable String rolePath, @PathVariable Long id, Model model) {
        User user = userService.getById(id);
        List<Department> departments = departmentService.getAllDepartments();
        String pageTitle = "";
        String roleLabel = "";
        String role = "";

        RoleInfo info = getRoleInfo(rolePath);
        if (info == null) {
            return "error/404";
        }
        model.addAttribute("pageTitle", info.pageTitle + " Detayları");
        model.addAttribute("roleLabel", info.roleLabel);
        model.addAttribute("role", info.role);
        model.addAttribute("user", user);
        model.addAttribute("rolePath", rolePath);
        model.addAttribute("departments", departments);

        return "user/user-details";
    }

    @GetMapping("/{rolePath}/{id}/duzenle")
    public String showUserEditForm(@PathVariable String rolePath, @PathVariable Long id, Model model) {
        User user = userService.getById(id);
        List<Department> departments = departmentService.getAllDepartments();
        String pageTitle = "";
        String roleLabel = "";
        String role = "";

        RoleInfo info = getRoleInfo(rolePath);
        if (info == null) {
            return "error/404";
        }
        model.addAttribute("pageTitle", info.pageTitle + " Düzenle");
        model.addAttribute("roleLabel", info.roleLabel);
        model.addAttribute("role", info.role);
        model.addAttribute("user", user);
        model.addAttribute("rolePath", rolePath);
        model.addAttribute("departments", departments);

        return "user/user-edit";
    }

    @PostMapping("/{rolePath}/{id}/duzenle")
    public String updateUser(
            @PathVariable String rolePath,
            @PathVariable Long id,
            @ModelAttribute("user") User user,
            @RequestParam(value = "receipt", required = false) MultipartFile receipt,
            RedirectAttributes redirectAttributes,
            Model model
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

    @GetMapping("/{rolePath}/ekle")
    public String showAddForm(@PathVariable String rolePath, Model model) {
        RoleInfo info = getRoleInfo(rolePath);
        if (info == null) {
            return "error/404";
        }
        model.addAttribute("pageTitle", info.pageTitle + " Ekle");
        model.addAttribute("roleLabel", info.roleLabel);
        model.addAttribute("role", info.role);
        model.addAttribute("rolePath", rolePath);
        model.addAttribute("departments", departmentService.getAllDepartments());
        User user = new User();
        user.setDepartment(new Department());
        model.addAttribute("user", user);
        return "user/user-add";
    }

    @PostMapping("/{rolePath}/ekle")
    public String addUser(
            @PathVariable String rolePath,
            @ModelAttribute("user") User user,
            @RequestParam(value = "receiptFile", required = false) MultipartFile receiptFile,
            RedirectAttributes redirectAttributes
    ) {
        RoleInfo info = getRoleInfo(rolePath);
        if (info == null) {
            redirectAttributes.addFlashAttribute("message", "Geçersiz rol!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/error/404";
        }
        try {
            if (receiptFile != null && !receiptFile.isEmpty()) {
                user.setReceipt(receiptFile.getBytes());
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            if (user.getDepartment() != null && user.getDepartment().getId() != null) {
                Department dept = departmentService.getDepartmentById(user.getDepartment().getId());
                user.setDepartment(dept);
            }

            Role role = roleService.getRoleByName(info.role);
            if (role != null) {
                user.setRole(role);
            }

            userService.save(user);

            redirectAttributes.addFlashAttribute("message", "Kullanıcı başarıyla eklendi.");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/" + rolePath;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Kullanıcı eklenirken bir hata oluştu!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/" + rolePath;
        }
    }
    @GetMapping("/dersler")
    public String listCourses(Model model) {
        List<Course> courses = courseService.getAllCoursesSorted("semester", "asc");
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        model.addAttribute("courses", courses);
        model.addAttribute("pageTitle", "Dersler");
        return "course/course-list";
    }

    @GetMapping("/dersler/{id}")
    public String showEditCourseForm(@PathVariable Long id, Model model,
                                     @RequestParam(name = "sort", required = false, defaultValue = "name") String sortField,
                                     @RequestParam(name = "dir", required = false, defaultValue = "asc") String sortDir) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ders bulunamadı: " + id));
        List<Department> departments = departmentService.getAllDepartments();
        List<User> instructors = userService.getAllInstructorsSorted(sortField, sortDir);
        model.addAttribute("course", course);
        model.addAttribute("departments", departments);
        model.addAttribute("instructors", instructors);
        model.addAttribute("pageTitle", course.getName());
        return "course/course-edit";
    }

    @PostMapping("/dersler/{id}/edit")
    public String updateCourse(@PathVariable Long id, @ModelAttribute("course") Course updatedCourse, Model model) {
        if (updatedCourse.getInstructor() == null || updatedCourse.getInstructor().getId() == null) {
            updatedCourse.setInstructor(null);
        }
        updatedCourse.setId(id);
        courseService.updateCourse(updatedCourse);
        model.addAttribute("pageTitle", updatedCourse.getName());
        return "redirect:/dersler";
    }

    @PostMapping("/dersler/{id}/delete")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/dersler";
    }

    @GetMapping("/dersler/ekle")
    public String showAddCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("pageTitle", "Ders Ekle");
        return "course/course-add";
    }

    @PostMapping("/dersler/ekle")
    public String addCourse(
            @ModelAttribute("course") Course course,
            RedirectAttributes redirectAttributes
    ) {
        Department dept = departmentService.getDepartmentById(course.getDepartment().getId());
        course.setDepartment(dept);
        courseService.saveCourse(course);

        redirectAttributes.addFlashAttribute("message", "Ders başarıyla eklendi!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/dersler";
    }

    private static class RoleInfo {
        public String pageTitle;
        public String roleLabel;
        public String role;

        public RoleInfo(String pageTitle, String roleLabel, String role) {
            this.pageTitle = pageTitle;
            this.roleLabel = roleLabel;
            this.role = role;
        }
    }

    private RoleInfo getRoleInfo(String rolePath) {
        switch (rolePath.toLowerCase()) {
            case "ogrenciler":
                return new RoleInfo("Öğrenci", "öğrenci", "student");
            case "egitmenler":
                return new RoleInfo("Eğitmen", "eğitmen", "instructor");
            case "yoneticiler":
                return new RoleInfo("Yönetici", "yönetici", "admin");
            default:
                return null;
        }
    }
    @GetMapping("/panel")
    public String showPanel(Model model) {
        List<SystemSetting> settings = systemSettingService.getAllSettings();
        model.addAttribute("pageTitle", "Panel");
        model.addAttribute("settings", settings);
        model.addAttribute("settingTitles", Map.of(
                "registration_open", "Ders Kaydı",
                "feedback_enabled", "Geri Bildirim",
                "document_request_active", "Belge Talebi",
                "club_announcements_open", "Kulüp Duyuruları",
                "maintenance_mode", "Bakım Modu",
                "event_registration_enabled", "Etkinlik Kaydı",
                "profile_edit_allowed", "Profil Düzenleme",
                "exam_schedule_visible", "Sınav Takvimi",
                "show_home_slider", "Anasayfa Slaytı",
                "enable_notifications", "Sistem Bildirimleri"
        ));
        return "home/panel";
    }
    @PostMapping("/admin/settings/update")
    public String updateSettings(@RequestParam("keys") List<String> keys,
                                 @RequestParam("values") List<String> values,
                                 RedirectAttributes redirectAttributes) {
        for (int i = 0; i < keys.size(); i++) {
            systemSettingService.updateValue(keys.get(i), values.get(i));
        }
        redirectAttributes.addFlashAttribute("success", "Ayarlar başarıyla güncellendi.");
        return "redirect:/panel";
    }


}
