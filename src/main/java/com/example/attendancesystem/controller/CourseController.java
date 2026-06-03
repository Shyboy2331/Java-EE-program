package com.example.attendancesystem.controller;

import com.example.attendancesystem.data.Course;
import com.example.attendancesystem.data.StudentCourse;
import com.example.attendancesystem.data.Result;
import com.example.attendancesystem.service.SignRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("/teacher/api/course")
public class CourseController {

    @Autowired
    private SignRecordService signRecordService;

    /**
     * 创建课程
     * POST /teacher/api/course/create
     */
    @PostMapping("/create")
    public Result<Course> createCourse(@RequestBody Map<String, Object> request) {
        try {
            String courseName = (String) request.get("courseName");
            Integer classCount = (Integer) request.get("classCount");
            String classTime = (String) request.get("classTime");
            String teacherId = (String) request.get("teacherId");
            String teacherName = (String) request.get("teacherName");

            if (courseName == null || courseName.isEmpty()) {
                return Result.error("课程名称不能为空");
            }
            if (classCount == null || classCount < 1 || classCount > 20) {
                return Result.error("班级个数必须在 1-20 之间");
            }
            if (classTime == null || classTime.isEmpty()) {
                return Result.error("上课时间不能为空");
            }

            Course course = signRecordService.createCourse(courseName, classCount, classTime, teacherId, teacherName);
            return Result.success(course);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取教师的所有课程
     * GET /teacher/api/course/list
     */
    @GetMapping("/list")
    public Result<List<Course>> getTeacherCourses(@RequestParam String teacherId) {
        try {
            if (teacherId == null || teacherId.isEmpty()) {
                return Result.error("教师 ID 不能为空");
            }
            // 需要通过 service 添加方法
            return Result.success(java.util.Collections.emptyList());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
