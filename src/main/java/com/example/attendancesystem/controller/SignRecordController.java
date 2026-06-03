package com.example.attendancesystem.controller;

import com.example.attendancesystem.data.Course;
import com.example.attendancesystem.data.SignRecord;
import com.example.attendancesystem.data.StudentCourse;
import com.example.attendancesystem.data.Result;
import com.example.attendancesystem.service.SignRecordExportService;
import com.example.attendancesystem.service.SignRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 打卡记录控制器
 */
@RestController
@RequestMapping("/student/api/sign")
public class SignRecordController {

    @Autowired
    private SignRecordService signRecordService;

    @Autowired
    private SignRecordExportService signRecordExportService;

    /**
     * 学生打卡
     * POST /student/api/sign/create
     */
    @PostMapping("/create")
    public Result<SignRecord> createSign(@RequestBody Map<String, String> request) {
        try {
            String studentId = request.get("studentId");
            String studentName = request.get("studentName");
            String courseId = request.get("courseId");
            String classId = request.get("classId");
            String remark = request.get("remark");

            if (studentId == null || studentId.isEmpty()) {
                return Result.error("学生 ID 不能为空");
            }
            if (courseId == null || courseId.isEmpty()) {
                return Result.error("课程 ID 不能为空");
            }

            SignRecord record = signRecordService.sign(studentId, studentName, courseId, classId, remark);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取学生的已选课程
     * GET /student/api/sign/courses
     */
    @GetMapping("/courses")
    public Result<List<StudentCourse>> getStudentCourses(@RequestParam String studentId) {
        try {
            if (studentId == null || studentId.isEmpty()) {
                return Result.error("学生 ID 不能为空");
            }
            List<StudentCourse> courses = signRecordService.getStudentCourses(studentId);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取学生的打卡历史
     * GET /student/api/sign/history
     */
    @GetMapping("/history")
    public Result<List<SignRecord>> getSignHistory(@RequestParam String studentId) {
        try {
            if (studentId == null || studentId.isEmpty()) {
                return Result.error("学生 ID 不能为空");
            }
            List<SignRecord> records = signRecordService.getStudentSignHistory(studentId);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取指定课程的打卡记录
     * GET /student/api/sign/course-records
     */
    @GetMapping("/course-records")
    public Result<List<SignRecord>> getCourseSignRecords(
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String teacherId) {
        try {
            List<SignRecord> records;

            // 如果 courseId 为"all"或空，获取所有打卡记录
            if (courseId == null || courseId.isEmpty() || "all".equals(courseId)) {
                // 获取所有打卡记录
                records = signRecordService.findAllSignRecords();
            } else {
                records = signRecordService.getCourseSignRecords(courseId);
            }

            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 导出打卡记录为 Excel 文件
     * GET /student/api/sign/export-excel
     */
    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportSignRecordsToExcel(
            @RequestParam(required = false) String courseId) {
        try {
            List<SignRecord> records;

            // 如果 courseId 为"all"或空，获取所有打卡记录
            if (courseId == null || courseId.isEmpty() || "all".equals(courseId)) {
                records = signRecordService.findAllSignRecords();
            } else {
                records = signRecordService.getCourseSignRecords(courseId);
            }

            // 生成 Excel 文件
            byte[] excelData = signRecordExportService.exportToExcel(records);

            // 生成文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = "打卡记录_" + timestamp + ".xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replace("+", "%20");

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.add("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
