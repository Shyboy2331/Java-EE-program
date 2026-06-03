package com.example.attendancesystem.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学生选课实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "student_course")
public class StudentCourse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 学生学号
     */
    @Column(name = "student_id", nullable = false)
    @JsonProperty("student_id")
    private String studentId;
    
    /**
     * 学生姓名
     */
    @Column(name = "student_name")
    @JsonProperty("student_name")
    private String studentName;
    
    /**
     * 课程 ID
     */
    @Column(name = "course_id", nullable = false)
    @JsonProperty("course_id")
    private String courseId;
    
    /**
     * 课程名称
     */
    @Column(name = "course_name")
    @JsonProperty("course_name")
    private String courseName;
    
    /**
     * 班级 ID
     */
    @Column(name = "class_id")
    @JsonProperty("class_id")
    private String classId;
    
    /**
     * 上课时间
     */
    @Column(name = "class_time")
    @JsonProperty("class_time")
    private String classTime;
    
    /**
     * 选课时间
     */
    @Column(name = "select_time")
    @JsonProperty("select_time")
    private LocalDateTime selectTime;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JsonProperty("create_time")
    private LocalDateTime createTime;
}
