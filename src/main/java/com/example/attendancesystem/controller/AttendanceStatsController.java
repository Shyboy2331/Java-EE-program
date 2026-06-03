package com.example.attendancesystem.controller;

import com.example.attendancesystem.data.*;
import com.example.attendancesystem.service.AttendanceStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤统计控制器
 */
@RestController
@RequestMapping("/api/stats")
public class AttendanceStatsController {

    @Autowired
    private AttendanceStatsService attendanceStatsService;

    /**
     * 获取按周统计的出勤率
     * GET /api/stats/weekly
     */
    @GetMapping("/weekly")
    public Result<List<AttendanceRateStats>> getWeeklyAttendanceRate(
            @RequestParam(required = false) String courseId,
            @RequestParam(defaultValue = "0") int weekOffset) {
        List<AttendanceRateStats> stats = attendanceStatsService.getWeeklyAttendanceRate(courseId, weekOffset);
        return Result.success(stats);
    }

    /**
     * 获取按月统计的出勤率
     * GET /api/stats/monthly
     */
    @GetMapping("/monthly")
    public Result<List<AttendanceRateStats>> getMonthlyAttendanceRate(
            @RequestParam(required = false) String courseId,
            @RequestParam(defaultValue = "0") int monthOffset) {
        List<AttendanceRateStats> stats = attendanceStatsService.getMonthlyAttendanceRate(courseId, monthOffset);
        return Result.success(stats);
    }

    /**
     * 获取班级出勤率统计
     * GET /api/stats/class
     */
    @GetMapping("/class")
    public Result<List<ClassAttendanceStats>> getClassAttendanceStats(
            @RequestParam(required = false) String courseId) {
        List<ClassAttendanceStats> stats = attendanceStatsService.getClassAttendanceStats(courseId);
        return Result.success(stats);
    }

    /**
     * 获取学生个人考勤统计
     * GET /api/stats/student
     */
    @GetMapping("/student")
    public Result<List<StudentAttendanceStats>> getStudentAttendanceStats(
            @RequestParam String studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<StudentAttendanceStats> stats = attendanceStatsService.getStudentAttendanceStats(studentId, startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 获取综合统计数据（用于主页）
     * GET /api/stats/summary
     */
    @GetMapping("/summary")
    public Result<Map<String, Object>> getSummaryStats(
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String studentId) {
        
        Map<String, Object> summary = new HashMap<>();
        
        if (studentId != null && !studentId.isEmpty()) {
            // 学生端统计
            LocalDate today = LocalDate.now();
            LocalDate monthStart = today.withDayOfMonth(1);
            
            List<StudentAttendanceStats> todayStats = attendanceStatsService.getStudentAttendanceStats(
                    studentId, today, today);
            List<StudentAttendanceStats> monthStats = attendanceStatsService.getStudentAttendanceStats(
                    studentId, monthStart, today);
            
            long normalCount = monthStats.stream().filter(s -> s.getStatus() == 1).count();
            long lateCount = monthStats.stream().filter(s -> s.getStatus() == 0).count();
            long totalCount = monthStats.size();
            
            summary.put("totalSignCount", totalCount);
            summary.put("monthSignCount", totalCount);
            summary.put("normalCount", normalCount);
            summary.put("lateCount", lateCount);
            summary.put("attendanceRate", totalCount > 0 ? (normalCount * 100.0 / totalCount) : 0);
            
        } else {
            // 教师端统计
            List<ClassAttendanceStats> classStats = attendanceStatsService.getClassAttendanceStats(courseId);
            
            int totalStudents = classStats.stream().mapToInt(ClassAttendanceStats::getStudentCount).sum();
            int todaySignCount = classStats.stream().mapToInt(ClassAttendanceStats::getTodaySignCount).sum();
            int lateCount = classStats.stream().mapToInt(ClassAttendanceStats::getLateCount).sum();
            
            summary.put("totalStudents", totalStudents);
            summary.put("todaySignCount", todaySignCount);
            summary.put("lateCount", lateCount);
            summary.put("attendanceRate", totalStudents > 0 ? (todaySignCount * 100.0 / totalStudents) : 0);
            summary.put("classCount", classStats.size());
        }
        
        return Result.success(summary);
    }
}
