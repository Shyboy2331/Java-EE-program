package com.example.attendancesystem.repository;

import com.example.attendancesystem.data.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    /**
     * 根据教师 ID 查询课程
     */
    List<Course> findByTeacherIdOrderByCreateTimeDesc(String teacherId);
    
    /**
     * 根据课程名称模糊查询
     */
    List<Course> findByCourseNameContaining(String courseName);
}
