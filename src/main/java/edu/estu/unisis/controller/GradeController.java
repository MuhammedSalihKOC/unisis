package edu.estu.unisis.controller;

import edu.estu.unisis.model.*;
import edu.estu.unisis.service.CourseExamWeightService;
import edu.estu.unisis.service.CourseService;
import edu.estu.unisis.service.GradeService;
import edu.estu.unisis.service.UserCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/derslerim/notlar")
public class GradeController {

    private final UserCourseService userCourseService;
    private final GradeService gradeService;
    private final CourseService courseService;
    private final CourseExamWeightService courseExamWeightService;

    @Autowired
    public GradeController(UserCourseService userCourseService, GradeService gradeService, CourseService courseService, CourseExamWeightService courseExamWeightService) {
        this.userCourseService = userCourseService;
        this.gradeService = gradeService;
        this.courseService = courseService;
        this.courseExamWeightService = courseExamWeightService;
    }
    @GetMapping("/{courseId}")
    public String showEditCourseForm(@PathVariable Long courseId, Model model) {
        List<ExamType> examTypes = courseExamWeightService.getExamTypesByCourseId(courseId);
        List<UserCourse> userCourses = userCourseService.getByCourseId(courseId);

        Map<Long, Map<Long, Grade>> userGradeMap = new HashMap<>();
        Map<Long, Double> weightedAverages = new HashMap<>();
        Map<Long, String> letterGrades = new HashMap<>();

        for (UserCourse uc : userCourses) {
            Long userId = uc.getUser().getId();

            Double average = gradeService.calculateWeightedAverage(uc.getGrades(), courseId);
            if (average != null) {
                weightedAverages.put(userId, average);
                letterGrades.put(userId, gradeService.getLetterGrade(average));
            }

            Map<Long, Grade> gradeMap = new HashMap<>();
            for (Grade grade : uc.getGrades()) {
                if (grade.getExamType() != null) {
                    gradeMap.put(grade.getExamType().getId(), grade);
                }
            }
            userGradeMap.put(userId, gradeMap);
        }

        model.addAttribute("examTypes", examTypes);
        model.addAttribute("userCourses", userCourses);
        model.addAttribute("userGradeMap", userGradeMap);
        model.addAttribute("weightedAverages", weightedAverages);
        model.addAttribute("letterGrades", letterGrades);
        model.addAttribute("pageTitle", "Notlar");

        return "course/grade";
    }

    @PostMapping("/guncelle")
    public String updateGrades(@RequestParam Long courseId,
                               @RequestParam Map<String, String> allParams,
                               RedirectAttributes redirectAttributes) {
        try {
            for (String paramKey : allParams.keySet()) {
                if (paramKey.startsWith("grades[")) {
                    // Örn: grades[5][1]
                    String[] parts = paramKey.substring(7, paramKey.length() - 1).split("\\]\\[");
                    Long userId = Long.parseLong(parts[0]);
                    Long examTypeId = Long.parseLong(parts[1]);

                    String valueStr = allParams.get(paramKey);
                    if (valueStr != null && !valueStr.isBlank()) {
                        Double gradeValue = Double.parseDouble(valueStr);
                        gradeService.saveOrUpdateGrade(userId, courseId, examTypeId, gradeValue);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("success", "Notlar başarıyla güncellendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }

        return "redirect:/derslerim/notlar/" + courseId;
    }




}
