package com.example.attendancesystem.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 周/月出勤率统计 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRateStats {
    /**
     * 周期标识（周几或日期）
     */
    private String period;
    
    /**
     * 应出勤天数
     */
    private int expectedDays;
    
    /**
     * 实际出勤天数
     */
    private int actualDays;
    
    /**
     * 迟到次数
     */
    private int lateCount;
    
    /**
     * 缺勤次数
     */
    private int absentCount;
    
    /**
     * 出勤率（百分比）
     */
    private double attendanceRate;
    
    /**
     * 总打卡次数
     */
    private int totalCount;
}
