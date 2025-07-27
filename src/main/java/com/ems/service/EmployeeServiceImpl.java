package com.ems.service;

import com.ems.model.Employee;
import com.ems.repository.EmployeeRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository repo;

    @Override
    public List<Employee> getAllEmployees() {
        return repo.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        return repo.save(employee);
    }

    @Override
    public void updateEmployee(Long id, Employee updatedEmployee) {
        Employee existing = repo.findById(id).orElse(null);
        if (existing != null) {
            existing.setName(updatedEmployee.getName());
            existing.setEmail(updatedEmployee.getEmail());
            existing.setDepartment(updatedEmployee.getDepartment());
            existing.setJobTitle(updatedEmployee.getJobTitle());
            existing.setDateOfJoining(updatedEmployee.getDateOfJoining());
            existing.setSalary(updatedEmployee.getSalary());
            repo.save(existing);
        }
    }

    @Override
    public void deleteEmployee(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Page<Employee> getPaginatedEmployees(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isEmpty()) {
            return repo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
        }
        return repo.findAll(pageable);
    }

    @Override
    public void exportToExcel(OutputStream out) throws IOException {
        String[] columns = {"ID", "Name", "Email", "Department", "Job Title", "Date of Joining", "Salary"};
        List<Employee> employees = repo.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLACK.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Employee emp : employees) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(emp.getId());
                row.createCell(1).setCellValue(emp.getName());
                row.createCell(2).setCellValue(emp.getEmail());
                row.createCell(3).setCellValue(emp.getDepartment());
                row.createCell(4).setCellValue(emp.getJobTitle());
                row.createCell(5).setCellValue(emp.getDateOfJoining() != null ? emp.getDateOfJoining().toString() : "");
                row.createCell(6).setCellValue(emp.getSalary());
            }

            workbook.write(out); // ✅ direct write
        }
    }

    @Override
    public Map<String, Long> getDepartmentEmployeeCount() {
        List<Object[]> results = repo.getDepartmentWiseEmployeeCount();

        return results.stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> (Long) obj[1]
                ));
    }


    @Override
    public List<Employee> getEmployeesByDateRange(LocalDate startDate, LocalDate endDate) {
        return repo.findByDateOfJoiningBetween(startDate, endDate);
    }


    @Override
    public Page<Employee> getPaginatedEmployees(int page, int size, String keyword, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);

        if ((startDate != null && endDate != null) && keyword != null && !keyword.isEmpty()) {
            return repo.findByNameOrEmailAndDateOfJoiningBetween(keyword, keyword, startDate, endDate, pageable);
        } else if (startDate != null && endDate != null) {
            return repo.findByDateOfJoiningBetween(startDate, endDate, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            return repo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
        }

        return repo.findAll(pageable);
    }


    @Override
    public List<Employee> searchEmployees(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return repo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
        }
        return repo.findAll();
    }

}