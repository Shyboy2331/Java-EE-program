package com.example.attendancesystem.repository;

import com.example.attendancesystem.data.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    
    /**
     * 根据学生 ID 查询选课记录
     */
    List<StudentCourse> findByStudentIdOrderBySelectTimeDesc(String studentId);
    
    /**
     * 根据学生 ID 和课程 ID 查询选课记录
     */
    List<StudentCourse> findByStudentIdAndCourseId(String studentId, String courseId);
    
    /**
     * 检查学生是否已选某课程
     */
    boolean existsByStudentIdAndCourseId(String studentId, String courseId);
}
