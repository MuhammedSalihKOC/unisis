package edu.estu.unisis.service;

import edu.estu.unisis.model.*;
import edu.estu.unisis.repository.CourseExamWeightRepository;
import edu.estu.unisis.repository.CourseRepository;
import edu.estu.unisis.repository.ExamTypeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseExamWeightManager implements CourseExamWeightService {

    private final CourseExamWeightRepository courseExamWeightRepository;
    private final ExamTypeRepository examTypeRepository;
    private final CourseRepository courseRepository;


    @Autowired
    public CourseExamWeightManager(CourseExamWeightRepository courseExamWeightRepository, ExamTypeRepository examTypeRepository, CourseRepository courseRepository) {
        this.courseExamWeightRepository = courseExamWeightRepository;
        this.examTypeRepository = examTypeRepository;
        this.courseRepository = courseRepository;
    }
    @Override
    public boolean validateTotalWeight(Long courseId) {
        Double total = courseExamWeightRepository.sumWeightByCourseId(courseId);
        double EPS = 0.0001;
        if (total == null || Math.abs(total - 100.0) > EPS) {
            return false;
        }
        return true;
    }
    @Override
    public Double getWeightPercentage(Long courseId, Long examTypeId) {
        return courseExamWeightRepository.findByCourseIdAndExamTypeId(courseId, examTypeId)
                .map(CourseExamWeight::getWeightPercentage)
                .orElseThrow(() -> new IllegalArgumentException("Ağırlık bulunamadı."));
    }

    @Override
    public List<ExamType> getExamTypesByCourseId(Long courseId) {
        return examTypeRepository.findExamTypesByCourseId(courseId);
    }

    @Transactional
    public void createOrUpdateWeight(CourseExamWeight weight, RedirectAttributes redirectAttributes) {
        courseExamWeightRepository.save(weight);
        validateTotalWeight(weight.getCourse().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Double> getWeightsByCourseAsMap(Long courseId) {
        List<CourseExamWeight> list = courseExamWeightRepository.findByCourseId(courseId);
        return list.stream().collect(
                Collectors.toMap(
                        cew -> cew.getExamType().getId(),
                        CourseExamWeight::getWeightPercentage,
                        (a, b) -> b,
                        LinkedHashMap::new
                )
        );
    }
    @Override
    public Double sumWeightByCourseId(Long courseId){
        return courseExamWeightRepository.sumWeightByCourseId(courseId);
    }

    @Override
    public List<CourseExamWeight> getByCourseId(Long courseId) {
        return courseExamWeightRepository.findByCourseId(courseId);
    }

    @Override
    public void saveOrUpdateWeight(Long courseId, Long examTypeId, Double courseExamWeightValue) {
        Optional<CourseExamWeight> examWeight = courseExamWeightRepository.findByCourseIdAndExamTypeId(courseId, examTypeId);
        CourseExamWeight courseExamWeight;

        if (examWeight.isPresent()) {
            courseExamWeight = examWeight.get();
            courseExamWeight.setWeightPercentage(courseExamWeightValue);
        } else {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course bulunamadı."));
            ExamType examType = examTypeRepository.findById(examTypeId)
                    .orElseThrow(() -> new RuntimeException("ExamType bulunamadı."));

            courseExamWeight = new CourseExamWeight();
            courseExamWeight.setCourse(course);
            courseExamWeight.setExamType(examType);
            courseExamWeight.setWeightPercentage(courseExamWeightValue);
        }

        courseExamWeightRepository.save(courseExamWeight);
    }

}
