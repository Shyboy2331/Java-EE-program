package com.example.attendancesystem.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导入错误记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportErrorRecord {
    /**
     * 行号（Excel 中的行号，从 1 开始）
     */
    private int rowNumber;
    
    /**
     * 学号
     */
    private String studentId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 原始数据行
     */
    private String rawData;
}
