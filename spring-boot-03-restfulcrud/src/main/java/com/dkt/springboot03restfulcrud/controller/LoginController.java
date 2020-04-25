package com.dkt.springboot03restfulcrud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {

    @PostMapping(value = "/login")
    public String login(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password,
                        Map<String, Object> map,
                        HttpSession session) {
        System.out.println("username:" + username);
        System.out.println("password:" + password);
        if ("admin".equals(username) && "123456".equals(password)) {
            session.setAttribute("user", username);
            return "redirect:/dashboard";
//            return "dashboard";
        } else {
            map.put("msg", "用户名或密码错误");
            return "index";
        }
    }

}
