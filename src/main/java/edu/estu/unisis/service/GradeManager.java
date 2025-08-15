package edu.estu.unisis.service;

import edu.estu.unisis.model.ExamType;
import edu.estu.unisis.model.Grade;
import edu.estu.unisis.model.UserCourse;
import edu.estu.unisis.repository.ExamTypeRepository;
import edu.estu.unisis.repository.GradeRepository;
import edu.estu.unisis.repository.UserCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class GradeManager implements GradeService {
    private final CourseExamWeightService courseExamWeightService;
    private final GradeRepository gradeRepository;
    private final ExamTypeRepository examTypeRepository;
    private final UserCourseRepository userCourseRepository;

    @Autowired
    public GradeManager(GradeRepository gradeRepository, ExamTypeRepository examTypeRepository, UserCourseRepository userCourseRepository, CourseExamWeightService courseExamWeightService) {
        this.gradeRepository = gradeRepository;
        this.examTypeRepository = examTypeRepository;
        this.userCourseRepository = userCourseRepository;
        this.courseExamWeightService = courseExamWeightService;
    }

    public Double calculateWeightedAverage(List<Grade> grades, Long courseId) {
        if (grades == null || grades.isEmpty()) return null;
        Map<Long, Double> weights = courseExamWeightService.getWeightsByCourseAsMap(courseId);
        for (Long examTypeId : weights.keySet()) {
            boolean exists = grades.stream().anyMatch(g ->
                    g.getExamType() != null &&
                            g.getExamType().getId().equals(examTypeId) &&
                            g.getGrade() != null
            );
            if (!exists) {
                return null;
            }
        }
        double totalWeighted = 0.0;
        for (Grade g : grades) {
            Double w = weights.get(g.getExamType().getId());
            totalWeighted += g.getGrade() * w;
        }
        return totalWeighted / 100.0;
    }
    @Override
    public String getLetterGrade(Double average) {
        if (average == null) return "N/A";
        if (average >= 90) return "AA";
        if (average >= 80) return "AB";
        if (average >= 70) return "BA";
        if (average >= 65) return "BB";
        if (average >= 60) return "BC";
        if (average >= 55) return "CB";
        if (average >= 50) return "CC";
        if (average >= 45) return "CD";
        if (average >= 40) return "DC";
        if (average >= 35) return "DD";
        return "FF";
    }
    @Override
    public void saveOrUpdateGrade(Long userId, Long courseId, Long examTypeId, Double gradeValue) {
        Optional<Grade> existing = gradeRepository.findByUserCourseUserIdAndUserCourseCourseIdAndExamTypeId(userId, courseId, examTypeId);

        Grade grade;
        if (existing.isPresent()) {
            grade = existing.get();
            grade.setGrade(gradeValue);
        } else {
            UserCourse uc = userCourseRepository.findByUserIdAndCourseId(userId, courseId)
                    .orElseThrow(() -> new RuntimeException("UserCourse bulunamadı."));
            ExamType et = examTypeRepository.findById(examTypeId)
                    .orElseThrow(() -> new RuntimeException("ExamType bulunamadı."));

            grade = new Grade();
            grade.setUserCourse(uc);
            grade.setExamType(et);
            grade.setGrade(gradeValue);
        }

        gradeRepository.save(grade);
    }

}

