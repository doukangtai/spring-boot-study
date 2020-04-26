package com.dkt.springboot04mybatis.mapper;

import com.dkt.springboot04mybatis.entity.Dog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface DogMapper {

    public Dog queryById(Integer id);

    public void insertDog(Dog dog);

}
