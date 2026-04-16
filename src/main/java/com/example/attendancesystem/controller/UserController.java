package com.example.attendancesystem.controller;

import com.example.attendancesystem.servise.StudentServise;
import com.example.attendancesystem.data.Result;
import com.example.attendancesystem.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private StudentServise studentServise;

    /**
     * 新增用户
     * POST /user/create
     */
    @PostMapping("/create")
    public Result<String> create(@RequestBody User user) {
        return Result.success(studentServise.createUser(user));
    }

    /**
     * 根据ID查询用户
     * GET /user/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(studentServise.getUserById(id));
    }

    /**
     * 根据用户名查询用户
     * GET /user/username/{username}
     */
    @GetMapping("/username/{username}")
    public Result<User> getByUsername(@PathVariable String username) {
        return Result.success(studentServise.getUserByUsername(username));
    }

    /**
     * 查询所有教师
     * GET /user/teachers
     */
    @GetMapping("/teachers")
    public Result<List<User>> getAllTeachers() {
        return Result.success(studentServise.getAllTeachers());
    }

    /**
     * 查询所有用户
     * GET /user/list
     */
    @GetMapping("/list")
    public Result<List<User>> getAllUsers() {
        return Result.success(studentServise.getAllUsers());
    }

    /**
     * 更新用户
     * PUT /user/update
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody User user) {
        return Result.success(studentServise.updateUser(user));
    }

    /**
     * 删除用户
     * DELETE /user/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return Result.success(studentServise.deleteUserById(id));
    }
}
