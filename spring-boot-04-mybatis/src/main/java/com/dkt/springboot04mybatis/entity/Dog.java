package com.dkt.springboot04mybatis.entity;

public class Dog {

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNaMe() {
        return naMe;
    }

    public void setNaMe(String naMe) {
        this.naMe = naMe;
    }

    private String naMe;
}
