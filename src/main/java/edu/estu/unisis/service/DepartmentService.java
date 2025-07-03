package edu.estu.unisis.service;

import edu.estu.unisis.model.Department;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService {
    List<Department> getAllDepartments();
    Department getDepartmentById(Long id);
    Department save(Department department);
    void deleteById(Long id);
}
