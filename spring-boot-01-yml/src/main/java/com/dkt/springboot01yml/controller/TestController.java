package com.dkt.springboot01yml.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

//    @Value("${person.age}")
    private Integer age;

    @RequestMapping("/test")
    public String test() {
        System.out.println(age);
        return "年龄：" + age;
    }

}
