package com.example.attendancesystem.signin;

import com.example.attendancesystem.data.SignRecord;
import com.example.attendancesystem.data.StudentCourse;
import com.example.attendancesystem.repository.SignRecordRepository;
import com.example.attendancesystem.repository.StudentCourseRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打卡签到核心类
 * 功能：
 * 1. 检索打卡时间
 * 2. 判断打卡情况（正常/迟到）
 * 3. 获取打卡 IP 地址
 * 4. 上传打卡记录
 */
@Component
public class Sign_in {

    @Autowired
    private SignRecordRepository signRecordRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private HttpServletRequest request;

    /**
     * 打卡状态常量
     */
    public static final int STATUS_NORMAL = 1;  // 正常打卡
    public static final int STATUS_LATE = 0;    // 迟到

    /**
     * 迟到阈值（分钟）
     * 延后 15 分钟内判断为打卡成功（正常），其余情况判断为迟到
     */
    public static final int LATE_THRESHOLD_MINUTES = 15;

    /**
     * 星期映射
     */
    private static final Map<String, Integer> WEEKDAY_MAP = new HashMap<>();
    static {
        WEEKDAY_MAP.put("星期一", 1);
        WEEKDAY_MAP.put("星期二", 2);
        WEEKDAY_MAP.put("星期三", 3);
        WEEKDAY_MAP.put("星期四", 4);
        WEEKDAY_MAP.put("星期五", 5);
        WEEKDAY_MAP.put("星期六", 6);
        WEEKDAY_MAP.put("星期日", 0);
    }

    /**
     * 获取当前打卡时间
     * @return 当前 LocalDateTime 时间
     */
    public LocalDateTime getCurrentSignTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前打卡 IP 地址
     * @return IP 地址字符串
     */
    public String getSignIpAddress() {
        // 获取真实 IP 地址（考虑代理情况）
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            // 如果是本地 IPv6 地址，转换为 IPv4
            if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
                ip = "127.0.0.1";
            }
        }
        // 如果有多个 IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 解析上课时间字符串
     * @param classTime 格式："星期 X HH:mm"
     * @return 包含星期和时间的数组 [weekday, hour, minute]
     */
    public int[] parseClassTime(String classTime) {
        if (classTime == null || classTime.trim().isEmpty()) {
            return null;
        }

        try {
            // 解析 "星期一 08:00" 格式
            String[] parts = classTime.trim().split("\\s+");
            if (parts.length < 2) {
                return null;
            }

            String weekdayStr = parts[0];
            String timeStr = parts[1];

            Integer weekday = WEEKDAY_MAP.get(weekdayStr);
            if (weekday == null) {
                return null;
            }

            String[] timeParts = timeStr.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            return new int[]{weekday, hour, minute};
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断打卡情况
     * @param signTime 打卡时间
     * @param classTime 上课时间（格式：星期 X HH:mm）
     * @return 打卡状态：STATUS_NORMAL(1)-正常，STATUS_LATE(0)-迟到
     */
    public int checkSignStatus(LocalDateTime signTime, String classTime) {
        int[] timeInfo = parseClassTime(classTime);
        if (timeInfo == null) {
            return STATUS_LATE; // 无法解析时间，默认为迟到
        }

        int weekday = timeInfo[0];
        int classHour = timeInfo[1];
        int classMinute = timeInfo[2];

        // 获取打卡时间的星期
        int signWeekday = signTime.getDayOfWeek().getValue();
        if (signWeekday == 7) {
            signWeekday = 0; // 星期日为 0
        }

        // 检查是否是同一天
        if (signWeekday != weekday) {
            // 不是上课日，根据具体情况判断
            // 这里简化处理：如果不是上课日打卡，也算正常（可能提前或延后上课）
            return STATUS_NORMAL;
        }

        // 计算上课时间点
        LocalTime classLocalTime = LocalTime.of(classHour, classMinute);
        LocalTime signLocalTime = signTime.toLocalTime();

        // 计算时间差（分钟）
        long minutesDiff = ChronoUnit.MINUTES.between(classLocalTime, signLocalTime);

        // 判断：
        // - 提前打卡或准时：正常
        // - 延后 15 分钟内：正常
        // - 延后超过 15 分钟：迟到
        if (minutesDiff <= LATE_THRESHOLD_MINUTES) {
            return STATUS_NORMAL;
        } else {
            return STATUS_LATE;
        }
    }

    /**
     * 获取打卡状态描述
     * @param status 打卡状态
     * @return 状态描述字符串
     */
    public String getStatusDescription(int status) {
        if (status == STATUS_NORMAL) {
            return "正常";
        } else if (status == STATUS_LATE) {
            return "迟到";
        }
        return "未知";
    }

    /**
     * 检索学生的已选课程
     * @param studentId 学生学号
     * @return 学生选课列表
     */
    public List<StudentCourse> getStudentCourses(String studentId) {
        return studentCourseRepository.findByStudentIdOrderBySelectTimeDesc(studentId);
    }

    /**
     * 创建打卡记录
     * @param studentId 学生学号
     * @param studentName 学生姓名
     * @param courseId 课程 ID
     * @param courseName 课程名称
     * @param classId 班级 ID
     * @param classTime 上课时间
     * @param remark 备注
     * @return 打卡记录
     */
    public SignRecord createSignRecord(String studentId, String studentName, 
                                        String courseId, String courseName,
                                        String classId, String classTime,
                                        String remark) {
        LocalDateTime signTime = getCurrentSignTime();
        int status = checkSignStatus(signTime, classTime);
        String ipAddress = getSignIpAddress();

        SignRecord record = new SignRecord();
        record.setStudentId(studentId);
        record.setStudentName(studentName);
        record.setCourseId(courseId);
        record.setCourseName(courseName);
        record.setClassId(classId);
        record.setClassTime(classTime);
        record.setSignTime(signTime);
        record.setStatus(status);
        record.setStatusDesc(getStatusDescription(status));
        record.setSignIp(ipAddress);
        record.setRemark(remark);
        record.setCreateTime(LocalDateTime.now());

        return signRecordRepository.save(record);
    }

    /**
     * 获取学生的打卡历史记录
     * @param studentId 学生学号
     * @return 打卡记录列表
     */
    public List<SignRecord> getStudentSignHistory(String studentId) {
        return signRecordRepository.findByStudentIdOrderBySignTimeDesc(studentId);
    }

    /**
     * 获取指定课程的打卡记录
     * @param courseId 课程 ID
     * @return 打卡记录列表
     */
    public List<SignRecord> getCourseSignRecords(String courseId) {
        return signRecordRepository.findByCourseIdOrderBySignTimeDesc(courseId);
    }

    /**
     * 统计学生指定日期范围内的打卡次数
     * @param studentId 学生学号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 打卡次数
     */
    public long countStudentSigns(String studentId, LocalDateTime startTime, LocalDateTime endTime) {
        return signRecordRepository.countByStudentIdAndSignTimeBetween(studentId, startTime, endTime);
    }

    /**
     * 统计学生指定日期范围内的正常打卡次数
     * @param studentId 学生学号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 正常打卡次数
     */
    public long countStudentNormalSigns(String studentId, LocalDateTime startTime, LocalDateTime endTime) {
        return signRecordRepository.countByStudentIdAndStatusAndSignTimeBetween(
            studentId, STATUS_NORMAL, startTime, endTime);
    }
}
