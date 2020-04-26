package com.dkt.springboot04mybatis.mapper;

import com.dkt.springboot04mybatis.entity.Emp;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EmpMapper {

    @Select("select * from emp")
    public List<Emp> queryAllEmp();

    @Select("select * from emp where id=#{id}")
    public Emp queryById(Integer id);

    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into emp(emp_name) values(#{empName})")
    public void insertEmp(Emp emp);

    @Delete("delete from emp where id=#{id}")
    public void deleteEmp(Integer id);

    @Update("update emp set emp_name=#{empName} where id=#{id}")
    public void updateEmp(Emp emp);

}
