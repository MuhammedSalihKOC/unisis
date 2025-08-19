package edu.estu.unisis.service;

import edu.estu.unisis.model.Exam;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExamService {
    Exam create(Exam exam, boolean enforceUniquePerType);
    Exam update(Long id, Exam changes);
    void delete(Long id);
    List<Exam> getAll();
    Optional<Exam> getById(Long id);
    List<Exam> getByCourse(Long courseId);
    List<Exam> getByDepartment(Long departmentId);
    boolean existsByCourseAndType(Long courseId, Long examTypeId);
    public List<Exam> getNearestExamDayExams();
    public List<Exam> getExamsForEarliestUpcomingDay();
}
