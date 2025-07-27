package com.ems.repository;

import com.ems.model.Employee;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e.department, COUNT(e) FROM Employee e GROUP BY e.department")
    List<Object[]> getDepartmentWiseEmployeeCount();

    List<Employee> findByDateOfJoiningBetween(LocalDate start, LocalDate end);
    Page<Employee> findByDateOfJoiningBetween(LocalDate start, LocalDate end, Pageable pageable);

    List<Employee> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
    Page<Employee> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);

    Page<Employee> findByNameOrEmailAndDateOfJoiningBetween(String name, String email, LocalDate start, LocalDate end, Pageable pageable);
}
