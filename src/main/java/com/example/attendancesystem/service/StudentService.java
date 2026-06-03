package com.example.attendancesystem.service;

import com.example.attendancesystem.data.Student;
import com.example.attendancesystem.data.User;

import java.util.List;
import java.util.Map;

public interface StudentService {
    // ===== 用户相关 =====
    String createUser(User user);

    User getUserById(Long userId);

    User getUserByUsername(String username);

    List<User> getAllTeachers();

    List<User> getAllUsers();

    String updateUser(User user);

    String deleteUserById(Long userId);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含 token 和用户信息）
     */
    Map<String, Object> login(String username, String password);

    /**
     * 带角色验证的用户登录
     * @param username 用户名
     * @param password 密码
     * @param role 角色（STUDENT 或 TEACHER），可为 null
     * @return 登录结果（包含 token 和用户信息）
     */
    Map<String, Object> loginWithRole(String username, String password, String role);

    /**
     * 退出登录
     * @param token 用户 token
     */
    void logout(String token);

    // ===== 学生相关 =====
    String createStudent(Student student);

    Student getStudentById(String studentId);

    List<Student> getAllStudents();

    String updateStudent(Student student);

    String deleteStudentById(String studentId);
}
