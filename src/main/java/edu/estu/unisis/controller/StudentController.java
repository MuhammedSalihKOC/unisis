package edu.estu.unisis.controller;

import edu.estu.unisis.model.User;
import edu.estu.unisis.service.StudentCourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudentController {

    private final StudentCourseService studentCourseService;

    public StudentController(StudentCourseService studentCourseService) {
        this.studentCourseService = studentCourseService;
    }

    @GetMapping("/derslerim")
    public String getStudentCourses(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("courses", studentCourseService.getCoursesOfStudent(loggedInUser.getId()));
        return "student-courses";
    }

}
