package edu.estu.unisis.service;

import edu.estu.unisis.model.ExamType;

import java.util.List;

public interface CourseExamWeightService {
    Double getWeightPercentage(Long courseId, Long examTypeId);
    List<ExamType> getExamTypesByCourseId(Long courseId);

}
