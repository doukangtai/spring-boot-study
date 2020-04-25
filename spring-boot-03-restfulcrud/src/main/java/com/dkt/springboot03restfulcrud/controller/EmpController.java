package com.dkt.springboot03restfulcrud.controller;

import com.dkt.springboot03restfulcrud.dao.DepartmentDao;
import com.dkt.springboot03restfulcrud.dao.EmployeeDao;
import com.dkt.springboot03restfulcrud.entities.Department;
import com.dkt.springboot03restfulcrud.entities.Employee;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
public class EmpController {

    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    DepartmentDao departmentDao;

    @GetMapping(value = "/emps")
    public String emps(Model model) {
        Collection<Employee> employees = employeeDao.getAll();
        model.addAttribute("emps", employees);
        return "list";
    }

    @GetMapping(value = "/emp")
    public String emp(Model model) {
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts", departments);
        return "add";
    }

    @PostMapping(value = "/addemp")
    public String addEmp(Employee employee) {
//        System.out.println(employee);
        employeeDao.save(employee);
        return "redirect:/emps";
    }

    @GetMapping(value = "/emp/{id}")
    public String toEditPage(@PathVariable(value = "id") Integer id,
                             Model model) {
        Employee employee = employeeDao.get(id);
        System.out.println("toEditPage:"+employee);
        model.addAttribute("emp", employee);
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts", departments);
        return "update";
    }

    @PutMapping(value = "/update")
    public String update(Employee employee) {
        System.out.println("update"+employee);
        employeeDao.save(employee);
        return "redirect:/emps";
    }

    @DeleteMapping(value = "/delete/{id}")
    public String delete(@PathVariable(value = "id") Integer id) {
        employeeDao.delete(id);
        return "redirect:/emps";
    }

}
