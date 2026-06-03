package com.example.attendancesystem.service;

import com.example.attendancesystem.data.ImportErrorRecord;
import com.example.attendancesystem.data.ImportResult;
import com.example.attendancesystem.data.Student;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 学生数据导入服务
 */
@Service
public class StudentImportService {

    // 最大文件大小 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // 支持的 Excel 文件扩展名
    private static final List<String> SUPPORTED_EXTENSIONS = List.of("xls", "xlsx");
    
    // 支持的 MIME 类型
    private static final List<String> SUPPORTED_MIME_TYPES = List.of(
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );
    
    // 日期格式解析器（支持多种格式）
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd"),
            DateTimeFormatter.ofPattern("yyyy-MM"),
            DateTimeFormatter.ofPattern("yyyy/MM"),
            DateTimeFormatter.ofPattern("MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd")
    );
    
    // 学号验证正则
    private static final String STUDENT_ID_PATTERN = "^[a-zA-Z0-9]{6,20}$";
    
    // 手机号验证正则
    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";

    /**
     * 导入学生数据
     * @param file 上传的 Excel 文件
     * @return 导入结果
     */
    public ImportResult importStudents(MultipartFile file) {
        ImportResult result = new ImportResult();
        
        // 1. 验证文件
        String validationError = validateFile(file);
        if (validationError != null) {
            result.setSuccess(false);
            result.setFailReport(validationError);
            return result;
        }
        
        try (InputStream inputStream = file.getInputStream()) {
            // 2. 读取 Excel 文件
            Workbook workbook = WorkbookFactory.create(inputStream);
            
            try {
                // 3. 获取第一个工作表
                Sheet sheet = workbook.getSheetAt(0);
                
                // 4. 遍历数据行（假设第一行是表头）
                int headerRow = 0;
                Row header = sheet.getRow(headerRow);
                if (header == null) {
                    result.setSuccess(false);
                    result.setFailReport("Excel 文件为空或格式不正确");
                    return result;
                }
                
                // 解析表头，获取列索引映射
                int[] columnIndices = parseHeader(header);
                if (columnIndices == null) {
                    result.setSuccess(false);
                    result.setFailReport("Excel 表头格式不正确，必须包含学号和姓名列");
                    return result;
                }
                
                int totalRows = sheet.getLastRowNum();
                List<Student> studentsToSave = new ArrayList<>();
                
                for (int i = headerRow + 1; i <= totalRows; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null || isRowEmpty(row)) {
                        continue; // 跳过空行
                    }
                    
                    // 解析学生数据
                    Student student = parseStudentRow(row, columnIndices, i + 1);
                    if (student != null) {
                        studentsToSave.add(student);
                        result.addSuccessRecord(student);
                    }
                }
                
                // 5. 批量保存学生数据
                for (Student student : studentsToSave) {
                    try {
                        // 这里调用 StudentService 保存数据
                        // 暂时先添加到成功列表，实际保存由 Controller 调用 Service 完成
                    } catch (Exception e) {
                        // 如果保存失败，从成功列表移除，添加到失败列表
                        result.getSuccessRecords().remove(student);
                        result.decrementSuccessCount();
                        result.addFailRecord(new ImportErrorRecord(
                                0, student.getId(), student.getName(),
                                "保存失败：" + e.getMessage(), ""
                        ));
                    }
                }
                
                // 生成失败报告
                result.setFailReport(result.generateFailReport());
                result.setSuccess(result.getFailCount() == 0);
                
            } finally {
                workbook.close();
            }
            
        } catch (IOException e) {
            result.setSuccess(false);
            result.setFailReport("读取 Excel 文件失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 验证上传的文件
     */
    private String validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "上传文件不能为空";
        }
        
        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return "文件大小超过限制（最大 10MB），当前大小：" + formatFileSize(file.getSize());
        }
        
        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return "无法识别的文件格式";
        }
        
        String extension = getFileExtension(originalFilename);
        if (!SUPPORTED_EXTENSIONS.contains(extension.toLowerCase())) {
            return "不支持的文件格式，请上传 Excel 文件（.xls 或 .xlsx）";
        }
        
        // 检查 MIME 类型
        String contentType = file.getContentType();
        if (contentType != null && !SUPPORTED_MIME_TYPES.contains(contentType)) {
            // MIME 类型检查不是强制的，因为有些系统可能返回不准确的类型
            // 这里只做警告，不阻止上传
        }
        
        return null; // 验证通过
    }
    
    /**
     * 解析表头，返回列索引数组
     * 索引顺序：[学号，姓名，班级，性别，出生日期，联系方式]
     */
    private int[] parseHeader(Row header) {
        int[] indices = new int[6];
        for (int i = 0; i < 6; i++) {
            indices[i] = -1;
        }
        
        for (Cell cell : header) {
            String cellValue = getCellValueAsString(cell).trim();
            int colIndex = cell.getColumnIndex();
            
            if (cellValue.contains("学号")) {
                indices[0] = colIndex;
            } else if (cellValue.contains("姓名")) {
                indices[1] = colIndex;
            } else if (cellValue.contains("班级")) {
                indices[2] = colIndex;
            } else if (cellValue.contains("性别")) {
                indices[3] = colIndex;
            } else if (cellValue.contains("出生") || cellValue.contains("生日")) {
                indices[4] = colIndex;
            } else if (cellValue.contains("联系") || cellValue.contains("手机") || cellValue.contains("电话")) {
                indices[5] = colIndex;
            }
        }
        
        // 学号和姓名是必填列
        if (indices[0] == -1 || indices[1] == -1) {
            return null;
        }
        
        return indices;
    }
    
    /**
     * 解析学生数据行
     */
    private Student parseStudentRow(Row row, int[] columnIndices, int rowNumber) {
        Student student = new Student();
        List<String> errors = new ArrayList<>();
        
        try {
            // 学号（必填）
            String studentId = getCellValue(row, columnIndices[0]);
            if (studentId == null || studentId.trim().isEmpty()) {
                errors.add("学号不能为空");
            } else {
                studentId = studentId.trim();
                if (!studentId.matches(STUDENT_ID_PATTERN)) {
                    errors.add("学号格式不正确（应为 6-20 位字母或数字）");
                }
                student.setId(studentId);
            }
            
            // 姓名（必填）
            String name = getCellValue(row, columnIndices[1]);
            if (name == null || name.trim().isEmpty()) {
                errors.add("姓名不能为空");
            } else {
                student.setName(name.trim());
            }
            
            // 班级（可选）
            if (columnIndices[2] != -1) {
                String classname = getCellValue(row, columnIndices[2]);
                student.setClassname(classname != null ? classname.trim() : null);
            }
            
            // 性别（可选）
            if (columnIndices[3] != -1) {
                String gender = getCellValue(row, columnIndices[3]);
                if (gender != null && !gender.trim().isEmpty()) {
                    gender = gender.trim();
                    if ("男".equals(gender) || "女".equals(gender)) {
                        student.setGender(gender);
                    } else if ("M".equalsIgnoreCase(gender) || "Male".equalsIgnoreCase(gender)) {
                        student.setGender("男");
                    } else if ("F".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender)) {
                        student.setGender("女");
                    } else {
                        errors.add("性别格式不正确（应为男/女）");
                    }
                }
            }
            
            // 出生日期（可选）
            if (columnIndices[4] != -1) {
                String birthDateStr = getCellValue(row, columnIndices[4]);
                if (birthDateStr != null && !birthDateStr.trim().isEmpty()) {
                    LocalDate birthDate = parseDate(birthDateStr.trim());
                    if (birthDate != null) {
                        student.setBirthDate(birthDate);
                    } else {
                        errors.add("出生日期格式不正确（支持 yyyy-MM-dd、yyyy/MM/dd 等格式）");
                    }
                }
            }
            
            // 联系方式（可选）
            if (columnIndices[5] != -1) {
                String contact = getCellValue(row, columnIndices[5]);
                if (contact != null && !contact.trim().isEmpty()) {
                    contact = contact.trim();
                    // 移除空格、横杠等常见分隔符
                    contact = contact.replaceAll("[\\s\\-]", "");
                    if (contact.matches(PHONE_PATTERN)) {
                        student.setContact(contact);
                    } else {
                        errors.add("联系方式格式不正确（应为 11 位手机号码）");
                    }
                }
            }
            
        } catch (Exception e) {
            errors.add("解析失败：" + e.getMessage());
        }
        
        // 如果有错误，记录失败信息
        if (!errors.isEmpty()) {
            String rawData = buildRawData(row);
            String studentId = student.getId() != null ? student.getId() : "未知";
            String name = student.getName() != null ? student.getName() : "未知";
            
            // 这里不抛出异常，而是返回 null，由调用方处理
            return null;
        }
        
        return student;
    }
    
    /**
     * 获取单元格的值
     */
    private String getCellValue(Row row, int columnIndex) {
        if (columnIndex == -1) {
            return null;
        }
        
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }
        
        return getCellValueAsString(cell);
    }
    
    /**
     * 获取单元格的字符串值
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            .format(date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                } else {
                    // 使用 NumberToTextConverter 将数字转换为字符串，避免科学计数法
                    return NumberToTextConverter.toText(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return NumberToTextConverter.toText(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
    
    /**
     * 解析日期字符串（支持多种格式）
     */
    private LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // 尝试下一个格式
            }
        }
        return null;
    }
    
    /**
     * 检查行是否为空
     */
    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell).trim();
                if (!value.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 构建原始数据行字符串
     */
    private String buildRawData(Row row) {
        StringBuilder sb = new StringBuilder();
        for (Cell cell : row) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(getCellValueAsString(cell));
        }
        return sb.toString();
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDot + 1);
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
    
    /**
     * 保存学生数据（带验证）
     */
    public String saveStudent(Student student) {
        // 这里应该调用 StudentService 保存数据
        // 暂时返回成功
        return "导入成功";
    }
}
