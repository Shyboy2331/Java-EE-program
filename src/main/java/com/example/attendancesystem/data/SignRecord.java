package com.example.attendancesystem.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 打卡记录实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sign_record")
public class SignRecord {
    
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
     * 打卡时间
     */
    @Column(name = "sign_time", nullable = false)
    @JsonProperty("sign_time")
    private LocalDateTime signTime;
    
    /**
     * 打卡状态：0-迟到，1-正常
     */
    @Column(name = "status", nullable = false)
    private Integer status;
    
    /**
     * 状态描述
     */
    @Column(name = "status_desc", length = 50)
    @JsonProperty("status_desc")
    private String statusDesc;
    
    /**
     * 打卡 IP 地址
     */
    @Column(name = "sign_ip", length = 50)
    @JsonProperty("sign_ip")
    private String signIp;
    
    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JsonProperty("create_time")
    private LocalDateTime createTime;
}
