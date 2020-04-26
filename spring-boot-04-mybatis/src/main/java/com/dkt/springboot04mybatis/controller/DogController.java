package com.dkt.springboot04mybatis.controller;

import com.dkt.springboot04mybatis.entity.Dog;
import com.dkt.springboot04mybatis.mapper.DogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DogController {

    @Autowired
    DogMapper dogMapper;

    @GetMapping(value = "/dog/{id}")
    public Dog queryById(@PathVariable(value = "id") Integer id) {
        Dog dog = dogMapper.queryById(id);
        return dog;
    }

    @GetMapping(value = "/dog/insert")
    public void insertDog(Dog dog) {
        dogMapper.insertDog(dog);
    }

}
