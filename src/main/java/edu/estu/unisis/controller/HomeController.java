package edu.estu.unisis.controller;

import edu.estu.unisis.model.Exam;
import edu.estu.unisis.service.ExamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {

    private final ExamService examService;

    public HomeController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/")
    public String homePage(HttpSession session, Model model) {
        model.addAttribute("nearestDayExams", examService.getExamsForEarliestUpcomingDay());
        model.addAttribute("pageTitle", "Ana Sayfa");
        return "home/home";
    }
}
