package edu.estu.unisis.service;

import edu.estu.unisis.model.CourseExamWeight;
import edu.estu.unisis.model.ExamType;
import edu.estu.unisis.repository.CourseExamWeightRepository;
import edu.estu.unisis.repository.ExamTypeRepository;
import edu.estu.unisis.service.CourseExamWeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseExamWeightManager implements CourseExamWeightService {

    private final CourseExamWeightRepository courseExamWeightRepository;
    private final ExamTypeRepository examTypeRepository;


    @Autowired
    public CourseExamWeightManager(CourseExamWeightRepository courseExamWeightRepository, ExamTypeRepository examTypeRepository) {
        this.courseExamWeightRepository = courseExamWeightRepository;
        this.examTypeRepository = examTypeRepository;
    }

    @Override
    public Double getWeightPercentage(Long courseId, Long examTypeId) {
        Optional<CourseExamWeight> weightOpt = courseExamWeightRepository.findByCourseIdAndExamTypeId(courseId, examTypeId);
        return weightOpt.map(CourseExamWeight::getWeightPercentage).orElse(null);
    }
    @Override
    public List<ExamType> getExamTypesByCourseId(Long courseId) {
        return examTypeRepository.findExamTypesByCourseId(courseId);
    }

}
