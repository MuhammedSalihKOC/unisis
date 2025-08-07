package edu.estu.unisis.controller;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.User;
import edu.estu.unisis.service.CourseService;
import edu.estu.unisis.service.UserCourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserCourseController {

    private final UserCourseService userCourseService;
    private final CourseService courseService;

    public UserCourseController(UserCourseService userCourseService, CourseService courseService) {
        this.userCourseService = userCourseService;
        this.courseService = courseService;
    }

    @GetMapping("/derslerim")
    public String getUserCourses(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("pageTitle", "Derslerim");

        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("courses", userCourseService.getCoursesOfUser(loggedInUser.getId()));
        return "course/user-courses";
    }

    @GetMapping("/dersler/kayit")
    public String showDersKayitPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Long userId = user.getId();
        Long departmentId = user.getDepartment().getId();

        List<Course> allCourses = courseService.getCoursesByDepartmentOrderBySemester(departmentId);
        List<Course> registeredCourses = userCourseService.getRegisteredCourses(userId);
        List<Long> registeredCourseIds = registeredCourses.stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        int totalCredits = userCourseService.getTotalCredits(userId);

        model.addAttribute("allCourses", allCourses);
        model.addAttribute("registeredCourseIds", registeredCourseIds);
        model.addAttribute("registeredCourses", registeredCourses);
        model.addAttribute("totalCredits", totalCredits);
        model.addAttribute("pageTitle", "Ders Kayıt");
        return "course/course-register";
    }

    @PostMapping("/dersler/ekle/{id}")
    public String addCourse(@PathVariable("id") Long courseId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Long userId = ((User) session.getAttribute("loggedInUser")).getId();
        try {
            userCourseService.registerCourse(userId, courseId);
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
        Long userId = ((User) session.getAttribute("loggedInUser")).getId();
        try {
            userCourseService.dropCourse(userId, courseId);
            redirectAttributes.addFlashAttribute("success", "Ders başarıyla silindi.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dersler/kayit";
    }


}
