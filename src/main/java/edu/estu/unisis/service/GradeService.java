package edu.estu.unisis.service;

import edu.estu.unisis.model.Grade;

import java.util.List;

public interface GradeService {
    public Double calculateWeightedAverage(List<Grade> grades, Long courseId);
    String getLetterGrade(Double average);
    void saveOrUpdateGrade(Long userId, Long courseId, Long examTypeId, Double gradeValue);

}
