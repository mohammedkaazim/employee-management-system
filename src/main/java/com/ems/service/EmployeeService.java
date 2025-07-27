package com.ems.service;

import com.ems.model.Employee;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    Employee saveEmployee(Employee employee);
    void updateEmployee(Long id, Employee updatedEmployee);
    void deleteEmployee(Long id);
    Map<String, Long> getDepartmentEmployeeCount();
    List<Employee> getEmployeesByDateRange(LocalDate startDate, LocalDate endDate);
    List<Employee> searchEmployees(String keyword);

    Page<Employee> getPaginatedEmployees(int page, int size, String keyword, LocalDate startDate, LocalDate endDate);

    // Pagination & Search
    Page<Employee> getPaginatedEmployees(int page, int size, String keyword);

    // ✅ Excel Export (Only this one!)
    void exportToExcel(OutputStream out) throws IOException;
}
