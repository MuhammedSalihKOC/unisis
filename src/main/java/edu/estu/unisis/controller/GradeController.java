package edu.estu.unisis.controller;

import edu.estu.unisis.model.*;
import edu.estu.unisis.service.CourseExamWeightService;
import edu.estu.unisis.service.CourseService;
import edu.estu.unisis.service.GradeService;
import edu.estu.unisis.service.UserCourseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

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

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes ra) {
        String referer = request.getHeader("Referer");
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:" + (referer != null ? referer : "/");
    }

    @GetMapping("/{courseId}")
    public String showCourseForm(@PathVariable Long courseId, Model model) {
        Course course = courseService.getById(courseId)
                .orElseThrow(() -> new RuntimeException("Course bulunamadı: " + courseId));

        List<ExamType> examTypes = courseExamWeightService.getExamTypesByCourseId(courseId);
        List<UserCourse> userCourses = userCourseService.getByCourseId(courseId);
        List<CourseExamWeight> examWeights = courseExamWeightService.getByCourseId(courseId);

        // examWeightsMap: key = examTypeId
        Map<Long, CourseExamWeight> examWeightsMap = examWeights.stream()
                .filter(w -> w.getExamType() != null)
                .collect(Collectors.toMap(w -> w.getExamType().getId(), w -> w, (a, b) -> a));

        // Notlar/ortalamalar
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

        double weightTotal = examWeights.stream()
                .map(CourseExamWeight::getWeightPercentage)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        boolean weightsAreValid = Math.abs(weightTotal - 100.0) < 0.01;
        boolean weightInvalid = !weightsAreValid;

        model.addAttribute("courseId", courseId);
        model.addAttribute("course", course);
        model.addAttribute("examTypes", examTypes);
        model.addAttribute("examWeights", examWeights);
        model.addAttribute("examWeightsMap", examWeightsMap);
        model.addAttribute("userCourses", userCourses);
        model.addAttribute("userGradeMap", userGradeMap);
        model.addAttribute("weightedAverages", weightedAverages);
        model.addAttribute("letterGrades", letterGrades);
        model.addAttribute("weightTotal", weightTotal);
        model.addAttribute("weightInvalid", weightInvalid);
        model.addAttribute("pageTitle", "Notlar");

        return "course/course-student";
    }

    @GetMapping("/{courseId}/duzenle")
    public String showEditCourseForm(@PathVariable Long courseId, Model model) {
        Course course = courseService.getById(courseId)
                .orElseThrow(() -> new RuntimeException("Course bulunamadı: " + courseId));
        List<ExamType> examTypes = courseExamWeightService.getExamTypesByCourseId(courseId);
        List<UserCourse> userCourses = userCourseService.getByCourseId(courseId);
        List<CourseExamWeight> examWeights = courseExamWeightService.getByCourseId(courseId);

        Map<Long, CourseExamWeight> examWeightsMap = examWeights.stream()
                .filter(w -> w.getExamType() != null)
                .collect(Collectors.toMap(w -> w.getExamType().getId(), w -> w, (a, b) -> a));

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

        double weightTotal = examWeights.stream()
                .map(CourseExamWeight::getWeightPercentage)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
        boolean weightInvalid = !courseExamWeightService.validateTotalWeight(courseId);



        // Model
        model.addAttribute("courseId", courseId);
        model.addAttribute("course", course);
        model.addAttribute("examTypes", examTypes);
        model.addAttribute("examWeights", examWeights);
        model.addAttribute("examWeightsMap", examWeightsMap);
        model.addAttribute("userCourses", userCourses);
        model.addAttribute("userGradeMap", userGradeMap);
        model.addAttribute("weightedAverages", weightedAverages);
        model.addAttribute("letterGrades", letterGrades);
        model.addAttribute("weightTotal", weightTotal);
        model.addAttribute("weightInvalid", weightInvalid);
        model.addAttribute("pageTitle", "Notlar");

        return "course/course-student-grade";
    }


    @PostMapping("/agirlik/guncelle")
    public String updateWeights(@RequestParam Long courseId,
                                @RequestParam("examTypeIds") List<Long> examTypeIds,
                                @RequestParam("weights") List<Double> weights,
                                RedirectAttributes redirectAttributes) {

        double total = weights.stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(total - 100.0) > 0.01) {
            redirectAttributes.addFlashAttribute("error", "Girdiğiniz ağırlıkların toplamı "+ total +"%. Toplam ağırlık 100%% olmalı.");
            return "redirect:/derslerim/notlar/" + courseId;
        }


        for (int i = 0; i < examTypeIds.size(); i++) {
            courseExamWeightService.saveOrUpdateWeight(courseId, examTypeIds.get(i), weights.get(i));
        }

        redirectAttributes.addFlashAttribute("success", "Ağırlıklar güncellendi.");
        return "redirect:/derslerim/notlar/" + courseId;
    }



    @PostMapping("/guncelle")
    public String updateGrades(@RequestParam Long courseId,
                               @RequestParam Map<String, String> allParams,
                               RedirectAttributes redirectAttributes) {
        try {
            for (String paramKey : allParams.keySet()) {
                if (paramKey.startsWith("grades[")) {
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
