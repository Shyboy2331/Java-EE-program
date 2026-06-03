package com.example.attendancesystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 考勤系统主启动类
 */
@SpringBootApplication
@RestController
public class AttendanceSystemApplication {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceSystemApplication.class);

    /**
     * 欢迎接口
     */
    @GetMapping("/hello")
    public String hello() {
        return "欢迎来到班级考勤管理系统";
    }

    /**
     * 关于信息
     */
    @GetMapping("/about")
    public String about() {
        return "黄巍然 计算机科学与技术";
    }

    public static void main(String[] args) {
        // 初始化日志文件夹
        initLogDirectory();
        
        SpringApplication.run(AttendanceSystemApplication.class, args);
    }

    /**
     * 初始化日志文件夹
     * 如果 logs 文件夹不存在，则创建它
     */
    private static void initLogDirectory() {
        Path logPath = Paths.get("logs");
        if (!logPath.toFile().exists()) {
            try {
                Files.createDirectories(logPath);
                System.out.println("日志文件夹创建成功：" + logPath.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("创建日志文件夹失败：" + e.getMessage());
            }
        }
    }
}
