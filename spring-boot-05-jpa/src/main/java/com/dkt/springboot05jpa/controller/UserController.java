package com.dkt.springboot05jpa.controller;

import com.dkt.springboot05jpa.dao.UserDao;
import com.dkt.springboot05jpa.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    UserDao userDao;

    @GetMapping(path = "/user/{id}")
    public Optional<User> findById(@PathVariable(value = "id") Integer id) {
        Optional<User> user = userDao.findById(id);
        return user;
    }

    @GetMapping(path = "/user")
    public User insert(User user) {
        User user1 = userDao.save(user);
        return user1;
    }

}
