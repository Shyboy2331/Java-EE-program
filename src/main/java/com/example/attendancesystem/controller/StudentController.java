package com.example.attendancesystem.controller;
import com.example.attendancesystem.servise.StudentServise;

import com.example.attendancesystem.Dao.StudentDao;
import com.example.attendancesystem.data.Result;
import com.example.attendancesystem.data.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/student")
    public class StudentController {
    @Autowired
    private StudentServise studentServise;
        @PostMapping("/create")
        public Result<String> create(@RequestBody Student student) {
            return Result.success(studentServise.createStudent(student));
        }
        @GetMapping("/Id")
        public Result<Student> getbyId(@RequestParam String id) {
            return Result.success(studentServise.getStudentById(id));
        }
    }