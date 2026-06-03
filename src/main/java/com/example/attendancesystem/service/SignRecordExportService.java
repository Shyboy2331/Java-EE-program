package com.example.attendancesystem.service;

import com.example.attendancesystem.data.SignRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 打卡记录导出服务
 */
@Service
public class SignRecordExportService {

    /**
     * 导出打卡记录为 Excel 文件
     * @param records 打卡记录列表
     * @return Excel 文件的字节数组
     * @throws IOException IO 异常
     */
    public byte[] exportToExcel(List<SignRecord> records) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            // 创建工作表
            Sheet sheet = workbook.createSheet("打卡记录");
            
            // 设置列宽
            sheet.setColumnWidth(0, 15 * 256); // 学号
            sheet.setColumnWidth(1, 12 * 256); // 姓名
            sheet.setColumnWidth(2, 15 * 256); // 班级
            sheet.setColumnWidth(3, 20 * 256); // 课程
            sheet.setColumnWidth(4, 20 * 256); // 打卡时间
            sheet.setColumnWidth(5, 10 * 256); // 状态
            sheet.setColumnWidth(6, 15 * 256); // IP 地址
            
            // 创建标题行
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("学生打卡记录表");
            
            // 合并标题单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
            
            // 设置标题样式
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleCell.setCellStyle(titleStyle);
            
            // 创建信息行（生成时间）
            Row infoRow = sheet.createRow(1);
            Cell infoCell = infoRow.createCell(0);
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日 HH:mm:ss"));
            infoCell.setCellValue("生成时间：" + now);
            
            // 设置信息行样式
            CellStyle infoStyle = workbook.createCellStyle();
            Font infoFont = workbook.createFont();
            infoFont.setFontHeightInPoints((short) 10);
            infoFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            infoStyle.setFont(infoFont);
            infoCell.setCellStyle(infoStyle);
            
            // 创建表头
            Row headerRow = sheet.createRow(2);
            String[] headers = {"学号", "姓名", "班级", "课程名称", "打卡时间", "状态", "IP 地址"};
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 创建数据行
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            CellStyle statusNormalStyle = workbook.createCellStyle();
            statusNormalStyle.cloneStyleFrom(dataStyle);
            Font normalFont = workbook.createFont();
            normalFont.setColor(IndexedColors.GREEN.getIndex());
            normalFont.setBold(true);
            statusNormalStyle.setFont(normalFont);
            
            CellStyle statusLateStyle = workbook.createCellStyle();
            statusLateStyle.cloneStyleFrom(dataStyle);
            Font lateFont = workbook.createFont();
            lateFont.setColor(IndexedColors.ORANGE.getIndex());
            lateFont.setBold(true);
            statusLateStyle.setFont(lateFont);
            
            int rowNum = 3;
            for (SignRecord record : records) {
                Row row = sheet.createRow(rowNum++);
                
                // 学号
                Cell idCell = row.createCell(0);
                idCell.setCellValue(record.getStudentId() != null ? record.getStudentId() : "-");
                idCell.setCellStyle(dataStyle);
                
                // 姓名
                Cell nameCell = row.createCell(1);
                nameCell.setCellValue(record.getStudentName() != null ? record.getStudentName() : "-");
                nameCell.setCellStyle(dataStyle);
                
                // 班级
                Cell classCell = row.createCell(2);
                String className = record.getClassId() != null ? record.getClassId() : "-";
                classCell.setCellValue(className);
                classCell.setCellStyle(dataStyle);
                
                // 课程名称
                Cell courseCell = row.createCell(3);
                courseCell.setCellValue(record.getCourseName() != null ? record.getCourseName() : "-");
                courseCell.setCellStyle(dataStyle);
                
                // 打卡时间
                Cell timeCell = row.createCell(4);
                if (record.getSignTime() != null) {
                    timeCell.setCellValue(record.getSignTime().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                } else {
                    timeCell.setCellValue("-");
                }
                timeCell.setCellStyle(dataStyle);
                
                // 状态
                Cell statusCell = row.createCell(5);
                String status = record.getStatus() != null && record.getStatus() == 1 ? "正常" : "迟到";
                statusCell.setCellValue(status);
                statusCell.setCellStyle(record.getStatus() != null && record.getStatus() == 1 
                    ? statusNormalStyle : statusLateStyle);
                
                // IP 地址
                Cell ipCell = row.createCell(6);
                ipCell.setCellValue(record.getSignIp() != null ? record.getSignIp() : "-");
                ipCell.setCellStyle(dataStyle);
            }
            
            // 创建统计行
            Row statsRow = sheet.createRow(rowNum++);
            statsRow.createCell(0).setCellValue("总记录数：" + records.size());
            
            long normalCount = records.stream().filter(r -> r.getStatus() != null && r.getStatus() == 1).count();
            long lateCount = records.size() - normalCount;
            
            statsRow.createCell(1).setCellValue("正常：" + normalCount);
            statsRow.createCell(2).setCellValue("迟到：" + lateCount);
            
            CellStyle statsStyle = workbook.createCellStyle();
            Font statsFont = workbook.createFont();
            statsFont.setBold(true);
            statsFont.setFontHeightInPoints((short) 10);
            statsStyle.setFont(statsFont);
            statsStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            statsStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            for (int i = 0; i < 3; i++) {
                Cell cell = statsRow.getCell(i);
                if (cell != null) {
                    cell.setCellStyle(statsStyle);
                }
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}
