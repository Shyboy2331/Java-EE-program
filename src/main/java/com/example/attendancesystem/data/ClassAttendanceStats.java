package com.example.attendancesystem.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 班级出勤率统计 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassAttendanceStats {
    /**
     * 班级 ID
     */
    private String classId;
    
    /**
     * 班级名称
     */
    private String className;
    
    /**
     * 课程 ID
     */
    private String courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 班级人数
     */
    private int studentCount;
    
    /**
     * 今日打卡人数
     */
    private int todaySignCount;
    
    /**
     * 今日出勤率
     */
    private double todayAttendanceRate;
    
    /**
     * 本周出勤率
     */
    private double weekAttendanceRate;
    
    /**
     * 本月出勤率
     */
    private double monthAttendanceRate;
    
    /**
     * 迟到人数
     */
    private int lateCount;
    
    /**
     * 缺勤人数
     */
    private int absentCount;
}
