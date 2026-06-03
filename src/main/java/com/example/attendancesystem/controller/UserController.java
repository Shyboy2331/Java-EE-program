package com.example.attendancesystem.controller;

import com.example.attendancesystem.service.StudentService;
import com.example.attendancesystem.data.Result;
import com.example.attendancesystem.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/create")
    public Result<String> create(@RequestBody User user) {
        return Result.success(studentService.createUser(user));
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(studentService.getUserById(id));
    }

    @GetMapping("/username/{username}")
    public Result<User> getByUsername(@PathVariable String username) {
        return Result.success(studentService.getUserByUsername(username));
    }

    @GetMapping("/teachers")
    public Result<List<User>> getAllTeachers() {
        return Result.success(studentService.getAllTeachers());
    }

    @GetMapping("/list")
    public Result<List<User>> getAllUsers() {
        return Result.success(studentService.getAllUsers());
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody User user) {
        return Result.success(studentService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return Result.success(studentService.deleteUserById(id));
    }
}
