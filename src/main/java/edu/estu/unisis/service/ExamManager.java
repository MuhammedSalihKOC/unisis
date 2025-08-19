package edu.estu.unisis.service;

import edu.estu.unisis.model.Exam;
import edu.estu.unisis.repository.ExamRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExamManager implements ExamService {

    private final ExamRepository examRepository;

    public ExamManager(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Exam create(Exam exam, boolean enforceUniquePerType) {
        if (enforceUniquePerType && examRepository.existsByCourseAndExamType(exam.getCourse().getId(), exam.getExamType().getId()))
            throw new IllegalArgumentException("Bu ders için bu sınav tipi zaten tanımlı.");
        return examRepository.save(exam);
    }

    @Override
    public Exam update(Long id, Exam changes) {
        Exam current = examRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Sınav bulunamadı."));
        if (changes.getCourse() != null) current.setCourse(changes.getCourse());
        if (changes.getExamType() != null) current.setExamType(changes.getExamType());
        current.setLocation(changes.getLocation());
        current.setNotes(changes.getNotes());
        return examRepository.save(current);
    }
    @Override
    public void delete(Long id) {
        if (!examRepository.existsById(id)) throw new IllegalArgumentException("Sınav bulunamadı.");
        examRepository.deleteById(id);
    }
    @Override
    public List<Exam> getAll() {
        return examRepository.findAll();
    }
    @Override
    public Optional<Exam> getById(Long id) {
        return examRepository.findById(id);
    }

    @Override
    public List<Exam> getByCourse(Long courseId) {
        return examRepository.findByCourseId(courseId);
    }
    @Override
    public List<Exam> getByDepartment(Long departmentId) {
        return examRepository.findByDepartmentId(departmentId);
    }

    @Override
    public boolean existsByCourseAndType(Long courseId, Long examTypeId) {
        return examRepository.existsByCourseAndExamType(courseId, examTypeId);
    }
    @Override
    public List<Exam> getNearestExamDayExams() {
        LocalDateTime now = LocalDateTime.now();

        var firstPage = examRepository
                .findByExamDatetimeAfterOrderByExamDatetimeAsc(now, PageRequest.of(0, 1));

        if (firstPage.isEmpty()) {
            return List.of();
        }

        Exam nearest = firstPage.getContent().get(0);
        LocalDate day = nearest.getExamDatetime().toLocalDate();

        LocalDateTime start = day.atStartOfDay();
        LocalDateTime endExclusive = day.plusDays(1).atStartOfDay(); // [start, end)

        return examRepository.findByExamDatetimeBetweenOrderByExamDatetimeAsc(start, endExclusive.minusNanos(1));
    }
    public List<Exam> getExamsForEarliestUpcomingDay() {
        LocalDateTime now = LocalDateTime.now();
        Exam first = examRepository.findFirstByExamDatetimeAfterOrderByExamDatetimeAsc(now);
        if (first == null) {
            return Collections.emptyList();
        }
        LocalDate targetDate = first.getExamDatetime().toLocalDate();
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        return examRepository.findByExamDatetimeBetweenOrderByExamDatetimeAsc(startOfDay, endOfDay);
    }

}
