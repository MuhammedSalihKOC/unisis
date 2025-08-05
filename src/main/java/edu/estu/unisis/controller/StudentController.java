package edu.estu.unisis.controller;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.User;
import edu.estu.unisis.service.CourseService;
import edu.estu.unisis.service.StudentCourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class StudentController {

    private final StudentCourseService studentCourseService;
    private final CourseService courseService;

    public StudentController(StudentCourseService studentCourseService, CourseService courseService) {
        this.studentCourseService = studentCourseService;
        this.courseService = courseService;
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
    @PostMapping("/dersler/ekle/{id}")
    public String addCourse(@PathVariable("id") Long courseId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Long studentId = ((User) session.getAttribute("loggedInUser")).getId();
        try {
            studentCourseService.registerCourse(studentId, courseId);
            redirectAttributes.addFlashAttribute("success", "Ders başarıyla eklendi.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dersler/kayit";
    }

    @PostMapping("/dersler/sil/{id}")
    public String removeCourse(@PathVariable("id") Long courseId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Long studentId = ((User) session.getAttribute("loggedInUser")).getId();
        try {
            studentCourseService.dropCourse(studentId, courseId);
            redirectAttributes.addFlashAttribute("success", "Ders başarıyla silindi.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dersler/kayit";
    }

    @GetMapping("/dersler/kayit")
    public String showDersKayitPage(Model model, HttpSession session) {
        User student = (User) session.getAttribute("loggedInUser");
        Long studentId = student.getId();
        Long departmentId = student.getDepartment().getId();

        List<Course> allCourses = courseService.getCoursesByDepartmentOrderBySemester(departmentId);
        List<Long> registeredCourseIds = studentCourseService.getRegisteredCourseIds(studentId);
        List<Course> registeredCourses = studentCourseService.getRegisteredCourses(studentId);
        int totalCredits = studentCourseService.getTotalCredits(studentId);

        model.addAttribute("allCourses", allCourses);
        model.addAttribute("registeredCourseIds", registeredCourseIds);
        model.addAttribute("registeredCourses", registeredCourses);
        model.addAttribute("totalCredits", totalCredits);
        model.addAttribute("pageTitle", "Ders Kayıt");
        return "course-register";
    }

}
