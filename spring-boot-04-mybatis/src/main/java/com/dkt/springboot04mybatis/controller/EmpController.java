package com.dkt.springboot04mybatis.controller;

import com.dkt.springboot04mybatis.entity.Emp;
import com.dkt.springboot04mybatis.mapper.EmpMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmpController {

    @Autowired
    EmpMapper empMapper;

    @GetMapping(value = "/all")
    public List<Emp> queryAllEmp() {
        List<Emp> emps = empMapper.queryAllEmp();
        return emps;
    }

    @GetMapping(value = "/query/{id}")
    public Emp queryById(@PathVariable(value = "id") Integer id) {
        Emp emp = empMapper.queryById(id);
        return emp;
    }

    @GetMapping(value = "/insert")
    public Emp insertEmp(Emp emp) {
        empMapper.insertEmp(emp);
        return emp;
    }

    @GetMapping(value = "/delete/{id}")
    public void deleteEmp(@PathVariable(value = "id") Integer id) {
        empMapper.deleteEmp(id);
    }

    @GetMapping(value = "/update")
    public void updateEmp(Emp emp) {
        empMapper.updateEmp(emp);
    }

}
