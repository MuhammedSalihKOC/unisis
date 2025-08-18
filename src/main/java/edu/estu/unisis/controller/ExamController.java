package edu.estu.unisis.controller;

import edu.estu.unisis.model.Exam;
import edu.estu.unisis.service.CourseService;
import edu.estu.unisis.service.DepartmentService;
import edu.estu.unisis.service.ExamService;
import edu.estu.unisis.service.ExamTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sinavlar")
public class ExamController {

    private final ExamService examService;
    private final CourseService courseService;
    private final ExamTypeService examTypeService;
    private final DepartmentService departmentService;

    public ExamController(ExamService examService, CourseService courseService, ExamTypeService examTypeService, DepartmentService departmentService) {
        this.examService = examService;
        this.courseService = courseService;
        this.examTypeService = examTypeService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String ShowExamList(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("examTypes", examTypeService.getAll());
        model.addAttribute("courses", courseService.getAll());
        model.addAttribute("exams", examService.getAll());
        model.addAttribute("pageTitle", "Sınavlar");
        return "exam/exam-list";
    }
    @GetMapping("/ekle")
    public String showCreateForm(Model model) {
        model.addAttribute("exam", new Exam());
        model.addAttribute("courses", courseService.getAll());
        model.addAttribute("examTypes", examTypeService.getAll());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("pageTitle", "Sınav Ekle");
        return "exam/exam-add";
    }

    @PostMapping("/ekle")
    public String create(@ModelAttribute("exam") Exam exam, RedirectAttributes ra) {
        try {
            examService.create(exam, true);
            ra.addFlashAttribute("success", "Sınav başarıyla eklendi.");
            return "redirect:/sinavlar";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/sinavlar/ekle";
        }
    }

    @GetMapping("/{id}/duzenle")
    public String showEditExamForm(@PathVariable Long id, Model model) {
        Exam exam = examService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sınav bulunamadı: " + id));

        model.addAttribute("exam", exam);
        model.addAttribute("courses", courseService.getAll());
        model.addAttribute("examTypes", examTypeService.getAll());
        model.addAttribute("pageTitle", "Sınav");

        return "exam/exam-edit";
    }

    @PostMapping("/{id}/duzenle")
    public String updateExam(@PathVariable Long id,
                             @ModelAttribute("exam") Exam examForm,
                             RedirectAttributes redirectAttributes) {

        Exam exam = examService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sınav bulunamadı: " + id));

        exam.setCourse(examForm.getCourse());
        exam.setExamType(examForm.getExamType());
        exam.setExamDatetime(examForm.getExamDatetime());
        exam.setLocation(examForm.getLocation());
        exam.setNotes(examForm.getNotes());

        examService.update(id, exam);

        redirectAttributes.addFlashAttribute("success", "Sınav başarıyla güncellendi.");
        return "redirect:/sinavlar";
    }

    @PostMapping("/{id}/sil")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            examService.delete(id);
            ra.addFlashAttribute("success", "Sınav silindi.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/sinavlar";
    }
}
