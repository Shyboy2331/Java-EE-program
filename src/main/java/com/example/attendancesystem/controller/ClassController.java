package com.example.attendancesystem.controller;

import com.example.attendancesystem.data.ClassUtils;
import com.example.attendancesystem.data.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级控制器
 * 提供班级相关的 API 接口
 */
@RestController
@RequestMapping("/api/class")
public class ClassController {

    /**
     * 根据课程信息生成班级列表
     * POST /api/class/generate
     */
    @PostMapping("/generate")
    public Result<List<ClassUtils>> generateClasses(@RequestBody ClassInfoRequest request) {
        try {
            String courseName = request.getCourseName();
            Integer classCount = request.getClassCount();
            String baseClassTime = request.getBaseClassTime();
            String teacherId = request.getTeacherId();
            String teacherName = request.getTeacherName();

            List<ClassUtils> classes = ClassUtils.generateClassesFromCourse(
                courseName, classCount, baseClassTime, teacherId, teacherName);
            
            return Result.success(classes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 解析上课时间
     * POST /api/class/parse-time
     */
    @PostMapping("/parse-time")
    public Result<TimeParseResult> parseClassTime(@RequestBody String classTime) {
        try {
            int[] timeInfo = ClassUtils.parseClassTime(classTime);
            if (timeInfo == null) {
                return Result.error("时间格式无效");
            }

            TimeParseResult result = new TimeParseResult();
            result.setWeekday(timeInfo[0]);
            result.setWeekdayName(ClassUtils.getWeekdayName(timeInfo[0]));
            result.setHour(timeInfo[1]);
            result.setMinute(timeInfo[2]);
            result.setFormatted(ClassUtils.formatClassTime(timeInfo[0], timeInfo[1], timeInfo[2]));

            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 验证上课时间格式
     * POST /api/class/validate-time
     */
    @PostMapping("/validate-time")
    public Result<Boolean> validateClassTime(@RequestBody String classTime) {
        try {
            boolean isValid = ClassUtils.isValidClassTime(classTime);
            return Result.success(isValid);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 生成班级 ID
     * GET /api/class/generate-id
     */
    @GetMapping("/generate-id")
    public Result<String> generateClassId(
            @RequestParam String courseName,
            @RequestParam int index) {
        try {
            String classId = ClassUtils.generateClassId(courseName, index);
            return Result.success(classId);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 判断是否迟到
     * POST /api/class/check-late
     */
    @PostMapping("/check-late")
    public Result<Boolean> checkLate(@RequestBody LateCheckRequest request) {
        try {
            boolean isLate = ClassUtils.isLate(request.getSignTime(), request.getClassTime(),
                                          request.getThresholdMinutes());
            return Result.success(isLate);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 班级信息请求 DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ClassInfoRequest {
        private String courseName;
        private Integer classCount;
        private String baseClassTime;
        private String teacherId;
        private String teacherName;
    }

    /**
     * 时间解析结果 DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class TimeParseResult {
        private Integer weekday;
        private String weekdayName;
        private Integer hour;
        private Integer minute;
        private String formatted;
    }

    /**
     * 迟到检查请求 DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class LateCheckRequest {
        private String signTime;
        private String classTime;
        private Integer thresholdMinutes;
    }
}
