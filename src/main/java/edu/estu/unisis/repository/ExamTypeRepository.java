package edu.estu.unisis.repository;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.ExamType;
import edu.estu.unisis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamTypeRepository extends JpaRepository<ExamType, Long> {

}
