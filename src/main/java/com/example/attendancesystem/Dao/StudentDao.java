package com.example.attendancesystem.Dao;

import com.example.attendancesystem.data.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.beans.BeanProperty;

@Repository
public class StudentDao {
    //@Autowired
    //private JdbcTemplate jdbcTemplate;

    public void insert(Student student) {
        String sql = "insert into student(student_Id,name,classname) values(?,?,?)";
        //jdbcTemplate.update(sql,student.getId(),student.getName(),student.getClass());
    }
    public Student findById(String id) {
        String sql = "select * from student where student_id=?";
        //return jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(Student.class));
        return null;
    }
}
