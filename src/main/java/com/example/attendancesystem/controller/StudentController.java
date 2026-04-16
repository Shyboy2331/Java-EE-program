package com.example.attendancesystem.controller;

import com.example.attendancesystem.servise.StudentServise;
import com.example.attendancesystem.data.Result;
import com.example.attendancesystem.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentServise studentServise;

    /**
     * 新增学生
     * POST /student/create
     */
    @PostMapping("/create")
    public Result<String> create(@RequestBody User user) {
        return Result.success(studentServise.createUser(user));
    }

    /**
     * 根据ID查询学生
     * GET /student/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(studentServise.getUserById(id));
    }

    /**
     * 查询所有学生
     * GET /student/list
     */
    @GetMapping("/list")
    public Result<List<User>> getAllStudents() {
        return Result.success(studentServise.getAllUsers());
    }

    /**
     * 更新学生
     * PUT /student/update
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody User user) {
        return Result.success(studentServise.updateUser(user));
    }

    /**
     * 删除学生
     * DELETE /student/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return Result.success(studentServise.deleteUserById(id));
    }
}
