package com.dkt.springboot01yml;

import com.dkt.springboot01yml.entity.Dog;
import com.dkt.springboot01yml.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringBoot01YmlApplicationTests {

    @Autowired
    Person person;
    @Autowired
    Dog dog;

    @Test
    void contextLoads() {
        System.out.println(person);
        System.out.println(dog);
    }

}
