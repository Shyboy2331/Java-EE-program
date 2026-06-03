package com.example.attendancesystem.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 学生个人考勤统计 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAttendanceStats {
    /**
     * 日期
     */
    private LocalDate date;
    
    /**
     * 课程 ID
     */
    private String courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 打卡状态：0-迟到，1-正常，2-缺勤
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 打卡时间
     */
    private String signTime;
    
    /**
     * 上课时间
     */
    private String classTime;
}
