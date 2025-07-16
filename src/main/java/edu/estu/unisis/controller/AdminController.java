    package edu.estu.unisis.controller;

    import edu.estu.unisis.model.Department;
    import edu.estu.unisis.model.Role;
    import edu.estu.unisis.model.User;
    import edu.estu.unisis.service.DepartmentService;
    import edu.estu.unisis.service.RoleService;
    import edu.estu.unisis.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.crypto.password.PasswordEncoder;
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
        private final RoleService roleService;
        private final PasswordEncoder passwordEncoder;

        @Autowired
        public AdminController(UserService userService, DepartmentService departmentService, RoleService roleService, PasswordEncoder passwordEncoder) {
            this.userService = userService;
            this.departmentService = departmentService;
            this.roleService = roleService;
            this.passwordEncoder = passwordEncoder;
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

            RoleInfo info = getRoleInfo(rolePath);
            if (info == null) {
                return "error/404";
            }
            model.addAttribute("pageTitle", info.pageTitle);
            model.addAttribute("roleLabel", info.roleLabel);
            model.addAttribute("role", info.role);
            model.addAttribute("user", user);
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
        @GetMapping("/{rolePath}/ekle")
        public String showAddForm(@PathVariable String rolePath, Model model) {
            RoleInfo info = getRoleInfo(rolePath);
            if (info == null) {
                return "error/404";
            }
            model.addAttribute("pageTitle", info.pageTitle);
            model.addAttribute("roleLabel", info.roleLabel);
            model.addAttribute("role", info.role);
            model.addAttribute("rolePath", rolePath);
            model.addAttribute("departments", departmentService.getAllDepartments());
            User user = new User();
            user.setDepartment(new Department());
            model.addAttribute("user", user);
            return "user-add";
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

                user.getRoles().clear();
                Role role = roleService.getRoleByName(info.role);
                if (role != null) {
                    user.getRoles().add(role);
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
    }
