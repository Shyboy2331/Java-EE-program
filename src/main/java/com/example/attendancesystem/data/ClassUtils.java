package com.example.attendancesystem.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级工具类
 * 包含班级相关的业务逻辑和工具方法
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassUtils {
    
    /**
     * 班级 ID
     */
    private String classId;
    
    /**
     * 课程 ID
     */
    private String courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 上课时间（格式：星期 X HH:mm）
     */
    private String classTime;
    
    /**
     * 授课教师
     */
    private String teacherName;
    
    /**
     * 上课地点
     */
    private String location;
    
    /**
     * 学生人数
     */
    private Integer studentCount;

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
     * 星期数组
     */
    private static final String[] WEEKDAY_NAMES = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * 根据课程信息生成多个班级
     * @param courseName 课程名称
     * @param classCount 班级个数
     * @param baseClassTime 基础上课时间（格式：星期 X HH:mm）
     * @param teacherId 教师 ID
     * @param teacherName 教师姓名
     * @return 班级列表
     */
    public static List<ClassUtils> generateClassesFromCourse(String courseName, Integer classCount,
                                                         String baseClassTime, String teacherId,
                                                         String teacherName) {
        List<ClassUtils> classes = new ArrayList<>();
        
        if (classCount == null || classCount < 1 || classCount > 20) {
            return classes;
        }
        
        // 解析基础上课时间
        int[] timeInfo = parseClassTime(baseClassTime);
        if (timeInfo == null) {
            return classes;
        }
        
        int baseWeekday = timeInfo[0];
        int baseHour = timeInfo[1];
        int baseMinute = timeInfo[2];
        
        // 生成多个班级，每个班级间隔 2 小时
        for (int i = 0; i < classCount; i++) {
            ClassUtils cls = new ClassUtils();
            cls.setClassId(generateClassId(courseName, i + 1));
            cls.setCourseName(courseName);
            cls.setTeacherName(teacherName);

            // 计算新的上课时间
            int newHour = baseHour + (i * 2);
            if (newHour > 20) {
                newHour = ((newHour - 21) % 12) + 8; // 循环
            }

            String classTime = String.format("%s %02d:%02d",
                WEEKDAY_NAMES[baseWeekday], newHour, baseMinute);
            cls.setClassTime(classTime);
            cls.setLocation("待定");
            cls.setStudentCount(0);

            classes.add(cls);
        }
        
        return classes;
    }

    /**
     * 解析上课时间字符串
     * @param classTime 格式："星期 X HH:mm"
     * @return 包含星期和时间的数组 [weekday, hour, minute]，解析失败返回 null
     */
    public static int[] parseClassTime(String classTime) {
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
     * 格式化上课时间
     * @param weekday 星期（0-6，0 为星期日）
     * @param hour 小时
     * @param minute 分钟
     * @return 格式化的时间字符串（星期 X HH:mm）
     */
    public static String formatClassTime(int weekday, int hour, int minute) {
        if (weekday < 0 || weekday > 6) {
            return null;
        }
        return String.format("%s %02d:%02d", WEEKDAY_NAMES[weekday], hour, minute);
    }

    /**
     * 生成班级 ID
     * @param courseName 课程名称
     * @param index 班级序号
     * @return 班级 ID
     */
    public static String generateClassId(String courseName, int index) {
        if (courseName == null || courseName.isEmpty()) {
            return String.format("CLS-%03d", index);
        }
        
        // 取课程名称的拼音首字母（简化处理，取前两个汉字）
        String prefix = "";
        int len = Math.min(courseName.length(), 2);
        for (int i = 0; i < len; i++) {
            char c = courseName.charAt(i);
            if (c >= '\u4e00' && c <= '\u9fa5') {
                // 汉字，取拼音首字母（简化处理）
                prefix += (char)('A' + (c % 26));
            } else {
                prefix += c;
            }
        }
        return String.format("%s-%03d", prefix, index);
    }

    /**
     * 根据星期几名称获取数字
     * @param weekdayName 星期几名称（如"星期一"）
     * @return 对应的数字（0-6），未知返回 null
     */
    public static Integer getWeekdayNumber(String weekdayName) {
        return WEEKDAY_MAP.get(weekdayName);
    }

    /**
     * 根据数字获取星期几名称
     * @param weekday 数字（0-6）
     * @return 星期几名称，无效输入返回 null
     */
    public static String getWeekdayName(int weekday) {
        if (weekday < 0 || weekday > 6) {
            return null;
        }
        return WEEKDAY_NAMES[weekday];
    }

    /**
     * 验证上课时间格式是否正确
     * @param classTime 上课时间字符串
     * @return 是否有效
     */
    public static boolean isValidClassTime(String classTime) {
        return parseClassTime(classTime) != null;
    }

    /**
     * 计算两个时间之间的分钟差
     * @param classTime1 时间 1
     * @param classTime2 时间 2
     * @return 分钟差，无法解析返回 0
     */
    public static int minutesBetween(String classTime1, String classTime2) {
        int[] time1 = parseClassTime(classTime1);
        int[] time2 = parseClassTime(classTime2);
        
        if (time1 == null || time2 == null) {
            return 0;
        }
        
        // 假设同一天，计算分钟差
        int minutes1 = time1[1] * 60 + time1[2];
        int minutes2 = time2[1] * 60 + time2[2];
        
        return minutes2 - minutes1;
    }

    /**
     * 判断是否迟到
     * @param signTime 打卡时间（格式：HH:mm）
     * @param classTime 上课时间（格式：星期 X HH:mm）
     * @param thresholdMinutes 迟到阈值（分钟）
     * @return true-迟到，false-正常
     */
    public static boolean isLate(String signTime, String classTime, int thresholdMinutes) {
        int[] classTimeInfo = parseClassTime(classTime);
        if (classTimeInfo == null) {
            return false;
        }
        
        try {
            String[] signParts = signTime.split(":");
            int signHour = Integer.parseInt(signParts[0]);
            int signMinute = Integer.parseInt(signParts[1]);
            
            int classMinutes = classTimeInfo[1] * 60 + classTimeInfo[2];
            int signMinutes = signHour * 60 + signMinute;
            
            return (signMinutes - classMinutes) > thresholdMinutes;
        } catch (Exception e) {
            return false;
        }
    }
}
