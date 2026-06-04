package com.example.attendancesystem.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "course")
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 课程名称
     */
    @Column(name = "course_name", nullable = false)
    @JsonProperty("course_name")
    private String courseName;
    
    /**
     * 班级个数
     */
    @Column(name = "class_count")
    @JsonProperty("class_count")
    private Integer classCount;
    
    /**
     * 上课时间（格式：星期 X HH:mm）
     */
    @Column(name = "class_time", nullable = false)
    @JsonProperty("class_time")
    private String classTime;
    
    /**
     * 授课教师 ID
     */
    @Column(name = "teacher_id")
    @JsonProperty("teacher_id")
    private Long teacherId;
    
    /**
     * 授课教师姓名
     */
    @Column(name = "teacher_name")
    @JsonProperty("teacher_name")
    private String teacherName;
    
    /**
     * 上课地点
     */
    @Column(name = "location", length = 100)
    private String location;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JsonProperty("create_time")
    private LocalDateTime createTime;
}
