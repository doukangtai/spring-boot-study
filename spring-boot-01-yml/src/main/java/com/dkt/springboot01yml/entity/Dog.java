package com.dkt.springboot01yml.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
//@ConfigurationProperties(prefix = "dog")
public class Dog {

    private String nickname;
    private Integer age;
//    @Value("${dog.parentsName}")
    private String parentsName;
//    @Value("${dog.children}")
    private List<String> children;

    @Override
    public String toString() {
        return "Dog{" +
                "nickname='" + nickname + '\'' +
                ", age=" + age +
                ", parentsName='" + parentsName + '\'' +
                ", children=" + children +
                '}';
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getParentsName() {
        return parentsName;
    }

    public void setParentsName(String parentsName) {
        this.parentsName = parentsName;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public Dog(String nickname, Integer age, String parentsName, List<String> children) {
        this.nickname = nickname;
        this.age = age;
        this.parentsName = parentsName;
        this.children = children;
    }

    public Dog() {
    }
}
