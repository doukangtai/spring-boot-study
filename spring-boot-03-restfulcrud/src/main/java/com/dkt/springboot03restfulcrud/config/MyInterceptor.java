package com.dkt.springboot03restfulcrud.config;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("user");
        if (user == null) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return false;
        } else {
            System.out.println("MyInterceptor:" + user);
            return true;
        }
    }
}
