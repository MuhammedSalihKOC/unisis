package edu.estu.unisis.service;

import edu.estu.unisis.model.CourseExamWeight;
import edu.estu.unisis.model.ExamType;

import java.util.List;
import java.util.Map;

public interface CourseExamWeightService {
    Double getWeightPercentage(Long courseId, Long examTypeId);
    List<ExamType> getExamTypesByCourseId(Long courseId);
    boolean validateTotalWeight(Long courseId);
    Map<Long, Double> getWeightsByCourseAsMap(Long courseId);
    Double sumWeightByCourseId(Long courseId);
    List<CourseExamWeight> getByCourseId(Long courseId);
    void saveOrUpdateWeight(Long courseId, Long examTypeId,Double courseExamWeightValue);


}
