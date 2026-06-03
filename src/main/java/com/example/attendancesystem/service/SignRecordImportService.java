package com.example.attendancesystem.service;

import com.example.attendancesystem.data.*;
import com.example.attendancesystem.repository.SignRecordRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 打卡记录导入服务
 */
@Service
public class SignRecordImportService {

    @Autowired
    private SignRecordService signRecordService;

    @Autowired
    private SignRecordRepository signRecordRepository;

    // 最大文件大小 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // 支持的 Excel 文件扩展名
    private static final List<String> SUPPORTED_EXTENSIONS = List.of("xls", "xlsx");
    
    // 支持的 MIME 类型
    private static final List<String> SUPPORTED_MIME_TYPES = List.of(
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );
    
    // 日期时间格式解析器（支持多种格式）
    private static final List<DateTimeFormatter> DATETIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd")
    );
    
    // 学号验证正则
    private static final String STUDENT_ID_PATTERN = "^[a-zA-Z0-9]{6,20}$";

    /**
     * 导入打卡记录
     * @param file 上传的 Excel 文件
     * @return 导入结果
     */
    public SignRecordImportResult importSignRecords(MultipartFile file) {
        SignRecordImportResult result = new SignRecordImportResult();
        
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
                    result.setFailReport("Excel 表头格式不正确，必须包含学号、姓名、课程 ID、打卡时间、状态列");
                    return result;
                }
                
                int totalRows = sheet.getLastRowNum();
                List<SignRecord> recordsToSave = new ArrayList<>();
                
                for (int i = headerRow + 1; i <= totalRows; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null || isRowEmpty(row)) {
                        continue; // 跳过空行
                    }
                    
                    // 解析打卡记录
                    SignRecord signRecord = parseSignRecordRow(row, columnIndices, i + 1);
                    if (signRecord != null) {
                        recordsToSave.add(signRecord);
                        result.addSuccessRecord(signRecord);
                    }
                }
                
                // 5. 批量保存打卡记录
                for (SignRecord record : recordsToSave) {
                    try {
                        // 验证学生是否存在
                        // 验证课程是否存在
                        // 这里暂时直接保存，实际应该调用 Service 验证
                        record.setCreateTime(LocalDateTime.now());
                        signRecordRepository.save(record);
                    } catch (Exception e) {
                        // 如果保存失败，从成功列表移除，添加到失败列表
                        result.getSuccessRecords().remove(record);
                        result.decrementSuccessCount();
                        result.addFailRecord(new SignRecordImportError(
                                0, record.getStudentId(), record.getStudentName(),
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
        
        return null; // 验证通过
    }
    
    /**
     * 解析表头，返回列索引数组
     * 索引顺序：[学号，姓名，课程 ID，课程名称，班级 ID，上课时间，打卡时间，状态，状态描述，IP 地址，备注]
     */
    private int[] parseHeader(Row header) {
        int[] indices = new int[11];
        for (int i = 0; i < 11; i++) {
            indices[i] = -1;
        }
        
        for (Cell cell : header) {
            String cellValue = getCellValueAsString(cell).trim();
            int colIndex = cell.getColumnIndex();
            
            if (cellValue.contains("学号")) {
                indices[0] = colIndex;
            } else if (cellValue.contains("姓名")) {
                indices[1] = colIndex;
            } else if (cellValue.contains("课程 ID") || cellValue.contains("course_id")) {
                indices[2] = colIndex;
            } else if (cellValue.contains("课程") && !cellValue.contains("ID")) {
                indices[3] = colIndex;
            } else if (cellValue.contains("班级 ID") || cellValue.contains("class_id")) {
                indices[4] = colIndex;
            } else if (cellValue.contains("上课时间") || cellValue.contains("class_time")) {
                indices[5] = colIndex;
            } else if (cellValue.contains("打卡时间") || cellValue.contains("sign_time")) {
                indices[6] = colIndex;
            } else if (cellValue.contains("状态") && !cellValue.contains("描述")) {
                indices[7] = colIndex;
            } else if (cellValue.contains("状态描述") || cellValue.contains("status")) {
                indices[8] = colIndex;
            } else if (cellValue.contains("IP") || cellValue.contains("ip")) {
                indices[9] = colIndex;
            } else if (cellValue.contains("备注")) {
                indices[10] = colIndex;
            }
        }
        
        // 学号、姓名、课程 ID、打卡时间、状态是必填列
        if (indices[0] == -1 || indices[1] == -1 || indices[2] == -1 || indices[6] == -1 || indices[7] == -1) {
            return null;
        }
        
        return indices;
    }
    
    /**
     * 解析打卡记录行
     */
    private SignRecord parseSignRecordRow(Row row, int[] columnIndices, int rowNumber) {
        SignRecord record = new SignRecord();
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
                record.setStudentId(studentId);
            }
            
            // 姓名（必填）
            String studentName = getCellValue(row, columnIndices[1]);
            if (studentName == null || studentName.trim().isEmpty()) {
                errors.add("姓名不能为空");
            } else {
                record.setStudentName(studentName.trim());
            }
            
            // 课程 ID（必填）
            if (columnIndices[2] != -1) {
                String courseId = getCellValue(row, columnIndices[2]);
                if (courseId != null && !courseId.trim().isEmpty()) {
                    record.setCourseId(courseId.trim());
                } else {
                    errors.add("课程 ID 不能为空");
                }
            }
            
            // 课程名称（可选）
            if (columnIndices[3] != -1) {
                String courseName = getCellValue(row, columnIndices[3]);
                record.setCourseName(courseName != null ? courseName.trim() : null);
            }
            
            // 班级 ID（可选）
            if (columnIndices[4] != -1) {
                String classId = getCellValue(row, columnIndices[4]);
                record.setClassId(classId != null ? classId.trim() : null);
            }
            
            // 上课时间（可选）
            if (columnIndices[5] != -1) {
                String classTime = getCellValue(row, columnIndices[5]);
                record.setClassTime(classTime != null ? classTime.trim() : null);
            }
            
            // 打卡时间（必填）
            if (columnIndices[6] != -1) {
                String signTimeStr = getCellValue(row, columnIndices[6]);
                if (signTimeStr != null && !signTimeStr.trim().isEmpty()) {
                    LocalDateTime signTime = parseDateTime(signTimeStr.trim());
                    if (signTime != null) {
                        record.setSignTime(signTime);
                    } else {
                        errors.add("打卡时间格式不正确（支持 yyyy-MM-dd HH:mm:ss 等格式）");
                    }
                } else {
                    errors.add("打卡时间不能为空");
                }
            }
            
            // 状态（必填）：0-迟到，1-正常
            if (columnIndices[7] != -1) {
                String statusStr = getCellValue(row, columnIndices[7]);
                if (statusStr != null && !statusStr.trim().isEmpty()) {
                    statusStr = statusStr.trim();
                    Integer status = parseStatus(statusStr);
                    if (status != null) {
                        record.setStatus(status);
                    } else {
                        errors.add("状态格式不正确（应为 0-迟到 或 1-正常）");
                    }
                } else {
                    errors.add("状态不能为空");
                }
            }
            
            // 状态描述（可选）
            if (columnIndices[8] != -1) {
                String statusDesc = getCellValue(row, columnIndices[8]);
                record.setStatusDesc(statusDesc != null ? statusDesc.trim() : null);
            }
            
            // IP 地址（可选）
            if (columnIndices[9] != -1) {
                String signIp = getCellValue(row, columnIndices[9]);
                record.setSignIp(signIp != null ? signIp.trim() : null);
            }
            
            // 备注（可选）
            if (columnIndices[10] != -1) {
                String remark = getCellValue(row, columnIndices[10]);
                record.setRemark(remark != null ? remark.trim() : null);
            }
            
        } catch (Exception e) {
            errors.add("解析失败：" + e.getMessage());
        }
        
        // 如果有错误，返回 null
        if (!errors.isEmpty()) {
            return null;
        }
        
        return record;
    }
    
    /**
     * 解析状态字符串
     */
    private Integer parseStatus(String statusStr) {
        // 支持数字和文字两种格式
        if ("0".equals(statusStr) || "迟到".equalsIgnoreCase(statusStr)) {
            return 0;
        } else if ("1".equals(statusStr) || "正常".equalsIgnoreCase(statusStr) || 
                   "出勤".equalsIgnoreCase(statusStr) || "出勤".equals(statusStr)) {
            return 1;
        }
        return null;
    }
    
    /**
     * 解析日期时间字符串（支持多种格式）
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        for (DateTimeFormatter formatter : DATETIME_FORMATTERS) {
            try {
                // 尝试解析日期时间
                try {
                    return LocalDateTime.parse(dateTimeStr, formatter);
                } catch (DateTimeParseException e) {
                    // 如果只包含日期，添加默认时间 00:00:00
                    LocalDate date = LocalDate.parse(dateTimeStr, formatter);
                    return date.atStartOfDay();
                }
            } catch (Exception e) {
                // 尝试下一个格式
            }
        }
        return null;
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
                    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            .format(date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                } else {
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
}
