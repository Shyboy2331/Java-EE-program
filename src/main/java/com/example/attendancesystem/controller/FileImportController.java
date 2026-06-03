package com.example.attendancesystem.controller;

import com.example.attendancesystem.data.*;
import com.example.attendancesystem.service.SignRecordImportService;
import com.example.attendancesystem.service.SignRecordService;
import com.example.attendancesystem.service.StudentImportService;
import com.example.attendancesystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件导入控制器
 */
@RestController
@RequestMapping("/api/import")
public class FileImportController {

    @Autowired
    private StudentImportService studentImportService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SignRecordImportService signRecordImportService;

    @Autowired
    private SignRecordService signRecordService;

    /**
     * 上传并导入学生数据
     * POST /api/import/students
     */
    @PostMapping(value = "/students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> importStudents(
            @RequestParam("file") MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 1. 使用 StudentImportService 解析 Excel 文件
        ImportResult importResult = studentImportService.importStudents(file);
        
        // 2. 保存成功解析的学生数据
        List<Student> successRecords = importResult.getSuccessRecords();
        for (Student student : successRecords) {
            try {
                String result = studentService.createStudent(student);
                if (!"创建成功".equals(result)) {
                    // 如果保存失败，从成功列表移除
                    importResult.getSuccessRecords().remove(student);
                    importResult.decrementSuccessCount();
                }
            } catch (Exception e) {
                // 保存失败，记录错误
                importResult.getSuccessRecords().remove(student);
                importResult.decrementSuccessCount();
                importResult.addFailRecord(
                    new com.example.attendancesystem.data.ImportErrorRecord(
                        0, 
                        student.getId(), 
                        student.getName(), 
                        "保存失败：" + e.getMessage(), 
                        ""
                    )
                );
            }
        }
        
        // 3. 更新成功状态
        importResult.setSuccess(importResult.getFailCount() == 0);
        importResult.setFailReport(importResult.generateFailReport());
        
        // 4. 构建响应数据
        response.put("success", importResult.isSuccess());
        response.put("totalCount", importResult.getTotalCount());
        response.put("successCount", importResult.getSuccessCount());
        response.put("failCount", importResult.getFailCount());
        response.put("failReport", importResult.getFailReport());
        response.put("successRecords", importResult.getSuccessRecords());
        response.put("failRecords", importResult.getFailRecords());
        
        return Result.success(response);
    }

    /**
     * 上传并导入打卡记录
     * POST /api/import/sign-records
     */
    @PostMapping(value = "/sign-records", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> importSignRecords(
            @RequestParam("file") MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 1. 使用 SignRecordImportService 解析 Excel 文件
        SignRecordImportResult importResult = signRecordImportService.importSignRecords(file);
        
        // 2. 更新成功状态
        importResult.setSuccess(importResult.getFailCount() == 0);
        importResult.setFailReport(importResult.generateFailReport());
        
        // 3. 构建响应数据
        response.put("success", importResult.isSuccess());
        response.put("totalCount", importResult.getTotalCount());
        response.put("successCount", importResult.getSuccessCount());
        response.put("failCount", importResult.getFailCount());
        response.put("failReport", importResult.getFailReport());
        response.put("successRecords", importResult.getSuccessRecords());
        response.put("failRecords", importResult.getFailRecords());
        
        return Result.success(response);
    }

    /**
     * 获取导入模板
     * GET /api/import/template
     */
    @GetMapping("/template")
    public Result<Map<String, Object>> getTemplate() {
        Map<String, Object> template = new HashMap<>();
        
        // 学生数据导入模板
        Map<String, String> studentTemplate = new HashMap<>();
        studentTemplate.put("templateDescription", "学生数据导入模板");
        studentTemplate.put("requiredFields", "学号、姓名");
        studentTemplate.put("optionalFields", "班级、性别、出生日期、联系方式");
        studentTemplate.put("formatRequirements", 
            "学号：6-20 位字母或数字\n" +
            "姓名：不能为空\n" +
            "性别：男/女\n" +
            "出生日期：yyyy-MM-dd 格式\n" +
            "联系方式：11 位手机号码");
        
        // 打卡记录导入模板
        Map<String, String> signRecordTemplate = new HashMap<>();
        signRecordTemplate.put("templateDescription", "打卡记录导入模板");
        signRecordTemplate.put("requiredFields", "学号、姓名、课程 ID、打卡时间、状态");
        signRecordTemplate.put("optionalFields", "课程名称、班级 ID、上课时间、状态描述、IP 地址、备注");
        signRecordTemplate.put("formatRequirements",
            "学号：6-20 位字母或数字\n" +
            "姓名：不能为空\n" +
            "课程 ID：数字\n" +
            "打卡时间：yyyy-MM-dd HH:mm:ss 格式\n" +
            "状态：0-迟到 或 1-正常");
        
        template.put("student", studentTemplate);
        template.put("signRecord", signRecordTemplate);
        
        return Result.success(template);
    }

    /**
     * 验证文件格式
     * POST /api/import/validate
     */
    @PostMapping(value = "/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> validateFile(
            @RequestParam("file") MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 验证文件大小
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            response.put("valid", false);
            response.put("message", "文件大小超过 10MB 限制");
            response.put("fileSize", formatFileSize(file.getSize()));
            return Result.success(response);
        }
        
        // 验证文件扩展名
        String filename = file.getOriginalFilename();
        if (filename == null || !(filename.endsWith(".xls") || filename.endsWith(".xlsx"))) {
            response.put("valid", false);
            response.put("message", "请上传 Excel 文件（.xls 或 .xlsx）");
            return Result.success(response);
        }
        
        // 验证文件内容
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = 
                org.apache.poi.ss.usermodel.WorkbookFactory.create(file.getInputStream());
            
            // 检查是否有工作表
            if (workbook.getNumberOfSheets() == 0) {
                response.put("valid", false);
                response.put("message", "Excel 文件没有工作表");
                workbook.close();
                return Result.success(response);
            }
            
            // 检查第一行是否为表头
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            org.apache.poi.ss.usermodel.Row header = sheet.getRow(0);
            if (header == null) {
                response.put("valid", false);
                response.put("message", "Excel 文件缺少表头");
                workbook.close();
                return Result.success(response);
            }
            
            // 检查是否包含必需的列
            boolean hasStudentId = false;
            boolean hasName = false;
            for (org.apache.poi.ss.usermodel.Cell cell : header) {
                String cellValue = cell.toString().trim();
                if (cellValue.contains("学号")) {
                    hasStudentId = true;
                }
                if (cellValue.contains("姓名")) {
                    hasName = true;
                }
            }
            
            if (!hasStudentId || !hasName) {
                response.put("valid", false);
                response.put("message", "Excel 表头必须包含\"学号\"和\"姓名\"列");
                workbook.close();
                return Result.success(response);
            }
            
            workbook.close();
            
            response.put("valid", true);
            response.put("message", "文件格式验证通过");
            response.put("fileSize", formatFileSize(file.getSize()));
            response.put("sheetCount", workbook.getNumberOfSheets());
            
        } catch (Exception e) {
            response.put("valid", false);
            response.put("message", "文件内容验证失败：" + e.getMessage());
        }
        
        return Result.success(response);
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
