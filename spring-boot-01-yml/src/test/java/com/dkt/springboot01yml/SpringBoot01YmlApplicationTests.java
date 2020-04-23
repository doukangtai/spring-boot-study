package com.dkt.springboot01yml;

import com.dkt.springboot01yml.entity.Dog;
import com.dkt.springboot01yml.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class SpringBoot01YmlApplicationTests {

    @Autowired
    ApplicationContext ioc;

    @Test
    void testIoc() {
        boolean b = ioc.containsBean("cat");
        System.out.println(b);
    }


    @Autowired
    Person person;
//    @Autowired
//    Dog dog;

    @Test
    void contextLoads() {
        System.out.println(person);
//        System.out.println(dog);
    }

}
