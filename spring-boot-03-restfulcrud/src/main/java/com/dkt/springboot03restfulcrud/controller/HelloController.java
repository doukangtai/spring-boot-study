package com.dkt.springboot03restfulcrud.controller;

import com.dkt.springboot03restfulcrud.exception.UserNotExistException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @ResponseBody
    @RequestMapping(value = "/hello")
    public String hello(@RequestParam(value = "user") String user) {
        if ("123".equals(user)) {
            throw new UserNotExistException();
        }
        return "Hello world";
    }

}
