package edu.estu.unisis.controller;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.Department;
import edu.estu.unisis.model.User;
import edu.estu.unisis.service.CourseService;
import edu.estu.unisis.service.DepartmentService;
import edu.estu.unisis.service.RoleService;
import edu.estu.unisis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CourseController {

    private final UserService userService;
    private final CourseService courseService;
    private final DepartmentService departmentService;


    @Autowired
    public CourseController(UserService userService, CourseService courseService, DepartmentService departmentService) {
        this.userService = userService;
        this.courseService = courseService;
        this.departmentService = departmentService;
    }

    @GetMapping("/dersler/{id}")
    public String showEditCourseForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ders bulunamadı: " + id));
        model.addAttribute("course", course);
        model.addAttribute("pageTitle", "Dersler");
        return "course";
    }
}
