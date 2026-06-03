package com.example.attendancesystem.controller;

import com.example.attendancesystem.data.StudentCourse;
import com.example.attendancesystem.data.Result;
import com.example.attendancesystem.service.SignRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学生选课控制器
 */
@RestController
@RequestMapping("/student/api/course")
public class StudentCourseController {

    @Autowired
    private SignRecordService signRecordService;

    /**
     * 学生选课
     * POST /student/api/course/select
     */
    @PostMapping("/select")
    public Result<StudentCourse> selectCourse(@RequestBody Map<String, String> request) {
        try {
            String studentId = request.get("studentId");
            String studentName = request.get("studentName");
            String courseId = request.get("courseId");
            String classId = request.get("classId");
            String classTime = request.get("classTime");

            if (studentId == null || studentId.isEmpty()) {
                return Result.error("学生 ID 不能为空");
            }
            if (courseId == null || courseId.isEmpty()) {
                return Result.error("课程 ID 不能为空");
            }

            StudentCourse studentCourse = signRecordService.selectCourse(
                studentId, studentName, courseId, classId, classTime);
            return Result.success(studentCourse);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取学生的已选课程
     * GET /student/api/course/list
     */
    @GetMapping("/list")
    public Result<java.util.List<StudentCourse>> getStudentCourses(@RequestParam String studentId) {
        try {
            if (studentId == null || studentId.isEmpty()) {
                return Result.error("学生 ID 不能为空");
            }
            java.util.List<StudentCourse> courses = signRecordService.getStudentCourses(studentId);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
