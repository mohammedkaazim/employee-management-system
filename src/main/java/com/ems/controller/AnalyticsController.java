package com.ems.controller;

import com.ems.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/department-count")
    public Map<String, Long> getDepartmentCount() {
        List<Object[]> results = employeeRepository.getDepartmentWiseEmployeeCount();
        Map<String, Long> chartData = new LinkedHashMap<>();

        for (Object[] row : results) {
            String dept = (String) row[0];
            Long count = (Long) row[1];
            chartData.put(dept, count);
        }

        return chartData;
    }
}
