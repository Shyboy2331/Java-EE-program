package com.example.attendancesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AttendanceSystemApplication {

    @GetMapping("/hello")
    public String hello() {
        return "欢迎来到班级考勤管理系统";
    }

    @GetMapping("/about")
    public String about() {
        return "黄巍然 计算机科学与技术";
    }

    public static void main(String[] args) {
        SpringApplication.run(AttendanceSystemApplication.class, args);
    }
}
