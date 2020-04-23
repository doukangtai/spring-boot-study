package com.dkt.springboot01yml.config;

import com.dkt.springboot01yml.entity.Cat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {

    // return待添加进容器中的Bean，容器中组件默认的id为方法名
    @Bean
    public Cat cat() {
        return new Cat();
    }

}
