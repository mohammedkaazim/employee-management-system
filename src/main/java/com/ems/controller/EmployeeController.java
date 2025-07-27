package com.ems.controller;

import com.ems.model.Employee;
import com.ems.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // ✅ Redirect root to /list
    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/employees/list";
    }

    // ✅ Show Add Form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "add-employee"; // 👈 make sure add-employee.html exists
    }

    // ✅ Save New Employee
    @PostMapping("/add")
    public String addEmployee(@ModelAttribute Employee employee) {
        employeeService.saveEmployee(employee);
        return "redirect:/employees/list";
    }

    // ✅ Show Edit Form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employee emp = employeeService.getEmployeeById(id);
        if (emp == null) {
            return "redirect:/employees/list";
        }
        model.addAttribute("employee", emp);
        return "edit-employee"; // 👈 make sure edit-employee.html exists
    }

    // ✅ Update Employee
    @PostMapping("/update/{id}")
    public String updateEmployee(@PathVariable Long id, @ModelAttribute Employee employee) {
        employeeService.updateEmployee(id, employee);
        return "redirect:/employees/list";
    }

    // ✅ Delete Employee
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employees/list";
    }

    // ✅ Export to Excel
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=employees.xlsx");

        employeeService.exportToExcel(response.getOutputStream());
    }

    @GetMapping("/charts")
    public String viewCharts(Model model) {
        Map<String, Long> deptCountMap = employeeService.getDepartmentEmployeeCount();
        model.addAttribute("departments", deptCountMap.keySet());
        model.addAttribute("counts", deptCountMap.values());
        return "employee-charts";
    }

    @GetMapping("/list")
    public String listEmployees(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "") String keyword,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        int pageSize = 5;
        Page<Employee> employeePage = employeeService.getPaginatedEmployees(page, pageSize, keyword, startDate, endDate);

        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "employees";
    }


    @GetMapping("/search")
    @ResponseBody
    public List<Employee> ajaxSearch(@RequestParam String keyword) {
        return employeeService.searchEmployees(keyword);
    }

}
