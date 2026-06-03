package com.example.attendancesystem.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件导入结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportResult {
    /**
     * 是否全部成功
     */
    private boolean success;
    
    /**
     * 成功导入数量
     */
    private int successCount;
    
    /**
     * 失败数量
     */
    private int failCount;
    
    /**
     * 成功导入的学生列表
     */
    private List<Student> successRecords = new ArrayList<>();
    
    /**
     * 失败记录列表
     */
    private List<ImportErrorRecord> failRecords = new ArrayList<>();
    
    /**
     * 导入失败报告（文本格式）
     */
    private String failReport;
    
    /**
     * 总记录数
     */
    public int getTotalCount() {
        return successCount + failCount;
    }
    
    /**
     * 添加成功记录
     */
    public void addSuccessRecord(Student student) {
        successRecords.add(student);
        successCount++;
    }
    
    /**
     * 添加失败记录
     */
    public void addFailRecord(ImportErrorRecord errorRecord) {
        failRecords.add(errorRecord);
        failCount++;
    }
    
    /**
     * 减少成功计数
     */
    public void decrementSuccessCount() {
        if (successCount > 0) {
            successCount--;
        }
    }
    
    /**
     * 生成失败报告
     */
    public String generateFailReport() {
        if (failRecords.isEmpty()) {
            return "无失败记录";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== 学生数据导入失败报告 ===\n\n");
        report.append("总失败数：").append(failCount).append("\n\n");
        report.append("详细错误信息：\n");
        report.append("-".repeat(80)).append("\n");
        
        for (ImportErrorRecord record : failRecords) {
            report.append("行号：").append(record.getRowNumber())
                  .append(" | 学号：").append(record.getStudentId() != null ? record.getStudentId() : "未知")
                  .append(" | 姓名：").append(record.getName() != null ? record.getName() : "未知")
                  .append("\n");
            report.append("  错误原因：").append(record.getErrorMessage()).append("\n");
            report.append("-".repeat(80)).append("\n");
        }
        
        return report.toString();
    }
}
