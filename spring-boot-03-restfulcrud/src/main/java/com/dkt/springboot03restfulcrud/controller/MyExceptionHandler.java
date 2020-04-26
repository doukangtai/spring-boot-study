package com.dkt.springboot03restfulcrud.controller;

import com.dkt.springboot03restfulcrud.exception.UserNotExistException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class MyExceptionHandler {

/*    @ResponseBody
    @ExceptionHandler(value = UserNotExistException.class)
    public Map<String, Object> myExceptionHandler(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", "user.not.exist.code");
        map.put("message", e.getMessage());
        return map;
    }*/

    @ExceptionHandler(value = UserNotExistException.class)
    public String myExceptionHandler(Exception e, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        request.setAttribute("javax.servlet.error.status_code", 500);
        map.put("code", "user.not.exist.code");
        map.put("message", e.getMessage());
        request.setAttribute("msg", map);
        return "forward:/error";
    }

}
