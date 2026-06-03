package com.example.attendancesystem.service;

import com.example.attendancesystem.data.*;
import com.example.attendancesystem.repository.SignRecordRepository;
import com.example.attendancesystem.repository.StudentCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 考勤统计服务
 */
@Service
public class AttendanceStatsService {

    @Autowired
    private SignRecordRepository signRecordRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    /**
     * 获取按周统计的出勤率
     * @param courseId 课程 ID
     * @param weekOffset 周偏移（0-本周，-1-上周，1-下周）
     * @return 周出勤率统计列表
     */
    public List<AttendanceRateStats> getWeeklyAttendanceRate(String courseId, int weekOffset) {
        List<AttendanceRateStats> result = new ArrayList<>();
        
        // 获取本周一
        LocalDate currentMonday = LocalDate.now()
                .plusWeeks(weekOffset)
                .with(DayOfWeek.MONDAY);
        
        // 一周 7 天
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = currentMonday.plusDays(i);
            LocalDateTime startOfDay = currentDate.atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            
            // 查询该日期的打卡记录
            List<SignRecord> records = signRecordRepository.findAll().stream()
                    .filter(r -> r.getSignTime() != null &&
                            r.getSignTime().isAfter(startOfDay) &&
                            r.getSignTime().isBefore(endOfDay) &&
                            (courseId == null || courseId.isEmpty() || courseId.equals(r.getCourseId())))
                    .collect(Collectors.toList());
            
            int totalCount = records.size();
            int normalCount = (int) records.stream().filter(r -> r.getStatus() == 1).count();
            int lateCount = (int) records.stream().filter(r -> r.getStatus() == 0).count();
            int absentCount = 0; // 缺勤需要额外计算
            
            double attendanceRate = totalCount > 0 ? (normalCount * 100.0 / totalCount) : 0;
            
            AttendanceRateStats stats = new AttendanceRateStats();
            stats.setPeriod(getWeekdayName(currentDate.getDayOfWeek()) + " (" + currentDate.format(DateTimeFormatter.ofPattern("MM-dd")) + ")");
            stats.setExpectedDays(1);
            stats.setActualDays(normalCount + lateCount);
            stats.setLateCount(lateCount);
            stats.setAbsentCount(absentCount);
            stats.setAttendanceRate(attendanceRate);
            stats.setTotalCount(totalCount);
            
            result.add(stats);
        }
        
        return result;
    }

    /**
     * 获取按月统计的出勤率
     * @param courseId 课程 ID
     * @param monthOffset 月偏移（0-本月，-1-上月）
     * @return 月出勤率统计列表
     */
    public List<AttendanceRateStats> getMonthlyAttendanceRate(String courseId, int monthOffset) {
        List<AttendanceRateStats> result = new ArrayList<>();
        
        // 获取本月第一天
        LocalDate currentMonth = LocalDate.now().plusMonths(monthOffset).withDayOfMonth(1);
        int daysInMonth = currentMonth.lengthOfMonth();
        
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = currentMonth.withDayOfMonth(day);
            LocalDateTime startOfDay = currentDate.atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            
            // 查询该日期的打卡记录
            List<SignRecord> records = signRecordRepository.findAll().stream()
                    .filter(r -> r.getSignTime() != null &&
                            r.getSignTime().isAfter(startOfDay) &&
                            r.getSignTime().isBefore(endOfDay) &&
                            (courseId == null || courseId.isEmpty() || courseId.equals(r.getCourseId())))
                    .collect(Collectors.toList());
            
            int totalCount = records.size();
            int normalCount = (int) records.stream().filter(r -> r.getStatus() == 1).count();
            int lateCount = (int) records.stream().filter(r -> r.getStatus() == 0).count();
            
            double attendanceRate = totalCount > 0 ? (normalCount * 100.0 / totalCount) : 0;
            
            AttendanceRateStats stats = new AttendanceRateStats();
            stats.setPeriod(currentDate.format(DateTimeFormatter.ofPattern("MM-dd")));
            stats.setExpectedDays(1);
            stats.setActualDays(normalCount + lateCount);
            stats.setLateCount(lateCount);
            stats.setAbsentCount(0);
            stats.setAttendanceRate(attendanceRate);
            stats.setTotalCount(totalCount);
            
            result.add(stats);
        }
        
        return result;
    }

    /**
     * 获取班级出勤率统计
     * @param courseId 课程 ID
     * @return 班级出勤率统计列表
     */
    public List<ClassAttendanceStats> getClassAttendanceStats(String courseId) {
        List<ClassAttendanceStats> result = new ArrayList<>();
        
        // 获取该课程的所有班级
        List<StudentCourse> studentCourses = studentCourseRepository.findAll().stream()
                .filter(sc -> courseId == null || courseId.isEmpty() || courseId.equals(sc.getCourseId()))
                .collect(Collectors.toList());
        
        // 按班级分组
        Map<String, List<StudentCourse>> classGroups = studentCourses.stream()
                .collect(Collectors.groupingBy(StudentCourse::getClassId));
        
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime weekStart = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        
        for (Map.Entry<String, List<StudentCourse>> entry : classGroups.entrySet()) {
            String classId = entry.getKey();
            List<StudentCourse> classStudents = entry.getValue();
            
            ClassAttendanceStats stats = new ClassAttendanceStats();
            stats.setClassId(classId);
            stats.setClassName(classId);
            stats.setCourseId(courseId);
            
            if (!classStudents.isEmpty()) {
                stats.setCourseName(classStudents.get(0).getCourseName());
            }
            
            stats.setStudentCount(classStudents.size());
            
            // 统计今日打卡人数
            int todaySignCount = 0;
            int lateCount = 0;
            
            for (StudentCourse sc : classStudents) {
                List<SignRecord> todayRecords = signRecordRepository.findAll().stream()
                        .filter(r -> r.getStudentId().equals(sc.getStudentId()) &&
                                r.getSignTime() != null &&
                                r.getSignTime().isAfter(today) &&
                                r.getSignTime().isBefore(tomorrow) &&
                                (courseId == null || courseId.isEmpty() || courseId.equals(r.getCourseId())))
                        .collect(Collectors.toList());
                
                if (!todayRecords.isEmpty()) {
                    todaySignCount++;
                    if (todayRecords.get(0).getStatus() == 0) {
                        lateCount++;
                    }
                }
            }
            
            stats.setTodaySignCount(todaySignCount);
            stats.setTodayAttendanceRate(classStudents.size() > 0 ? (todaySignCount * 100.0 / classStudents.size()) : 0);
            stats.setLateCount(lateCount);
            stats.setAbsentCount(classStudents.size() - todaySignCount);
            
            // 计算本周和本月出勤率（简化处理）
            stats.setWeekAttendanceRate(stats.getTodayAttendanceRate());
            stats.setMonthAttendanceRate(stats.getTodayAttendanceRate());
            
            result.add(stats);
        }
        
        return result;
    }

    /**
     * 获取学生个人考勤统计（按日期范围）
     * @param studentId 学生学号
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 学生考勤统计列表
     */
    public List<StudentAttendanceStats> getStudentAttendanceStats(String studentId, LocalDate startDate, LocalDate endDate) {
        List<StudentAttendanceStats> result = new ArrayList<>();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);
        
        // 查询该学生的打卡记录
        List<SignRecord> records = signRecordRepository.findByStudentIdAndSignTimeBetween(studentId, startDateTime, endDateTime);
        
        for (SignRecord record : records) {
            StudentAttendanceStats stats = new StudentAttendanceStats();
            stats.setDate(record.getSignTime().toLocalDate());
            stats.setCourseId(record.getCourseId());
            stats.setCourseName(record.getCourseName());
            stats.setStatus(record.getStatus());
            stats.setStatusDesc(record.getStatus() == 1 ? "正常" : "迟到");
            stats.setSignTime(record.getSignTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            stats.setClassTime(record.getClassTime());
            
            result.add(stats);
        }
        
        return result;
    }

    /**
     * 获取星期名称
     */
    private String getWeekdayName(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "周一";
            case TUESDAY: return "周二";
            case WEDNESDAY: return "周三";
            case THURSDAY: return "周四";
            case FRIDAY: return "周五";
            case SATURDAY: return "周六";
            case SUNDAY: return "周日";
            default: return "";
        }
    }
}
