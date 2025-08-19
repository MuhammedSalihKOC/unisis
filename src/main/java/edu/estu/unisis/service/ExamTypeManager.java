package edu.estu.unisis.service;

import edu.estu.unisis.model.ExamType;
import edu.estu.unisis.repository.ExamTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ExamTypeManager implements ExamTypeService {

    private final ExamTypeRepository examTypeRepository;

    public ExamTypeManager(ExamTypeRepository examTypeRepository) {
        this.examTypeRepository = examTypeRepository;
    }
    public List<ExamType> getAll(){
        return examTypeRepository.findAll();
    }
}
