package com.example.attendancesystem.controller;

import com.example.attendancesystem.service.StudentService;
import com.example.attendancesystem.data.Result;
import com.example.attendancesystem.data.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 用户登录
     * POST /student/api/login
     */
    @PostMapping("/api/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> request, HttpSession session) {
        String username = request.get("username");
        String password = request.get("password");
        String role = request.get("role");

        // 使用带角色验证的登录方法
        Map<String, Object> loginResult = studentService.loginWithRole(username, password, role);
        
        // 将用户信息存入 Session
        if (loginResult.get("user") != null) {
            session.setAttribute("currentUser", loginResult.get("user"));
        }
        
        return Result.success(loginResult);
    }

    /**
     * 退出登录
     * POST /student/api/logout
     */
    @PostMapping("/api/logout")
    public Result<String> logout(@RequestHeader("Authorization") String token, HttpSession session) {
        studentService.logout(token.replace("Bearer ", ""));
        // 清除 Session
        session.removeAttribute("currentUser");
        return Result.success("退出登录成功");
    }

    /**
     * 获取当前登录用户信息
     * GET /student/api/current-user
     */
    @GetMapping("/api/current-user")
    public Result<User> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("未登录");
        }
        return Result.success(user);
    }

    /**
     * 新增学生
     * POST /student/create
     */
    @PostMapping("/create")
    public Result<String> create(@RequestBody User user) {
        return Result.success(studentService.createUser(user));
    }

    /**
     * 根据 ID 查询学生
     * GET /student/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(studentService.getUserById(id));
    }

    /**
     * 查询所有学生
     * GET /student/list
     */
    @GetMapping("/list")
    public Result<List<User>> getAllStudents() {
        return Result.success(studentService.getAllUsers());
    }

    /**
     * 更新学生
     * PUT /student/update
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody User user) {
        return Result.success(studentService.updateUser(user));
    }

    /**
     * 删除学生
     * DELETE /student/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return Result.success(studentService.deleteUserById(id));
    }
}
