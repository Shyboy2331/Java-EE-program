package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.data.Student;
import com.example.attendancesystem.data.User;
import com.example.attendancesystem.repository.StudentRepository;
import com.example.attendancesystem.repository.UserRepository;
import com.example.attendancesystem.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HttpServletRequest request;

    // 简单的 token 存储（生产环境建议使用 Redis）
    private static final Map<String, User> tokenStore = new HashMap<>();

    // ===== 用户相关 =====

    @Override
    public String createUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        // 设置默认角色（如果未指定）
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT");
        } else {
            // 确保角色是有效的
            String role = user.getRole().toUpperCase();
            if (!"STUDENT".equals(role) && !"TEACHER".equals(role)) {
                throw new RuntimeException("无效的角色，只能是 STUDENT 或 TEACHER");
            }
            user.setRole(role);
        }
        // 设置创建时间
        user.setCreateTime(LocalDateTime.now());
        userRepository.save(user);
        return "创建成功";
    }

    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> getAllTeachers() {
        return userRepository.findByRole("TEACHER");
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public String updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.save(user);
        return "更新成功";
    }

    @Override
    public String deleteUserById(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(userId);
        return "删除成功";
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        return loginWithRole(username, password, null);
    }

    /**
     * 带身份验证的登录方法
     * @param username 用户名
     * @param password 密码
     * @param role 角色（STUDENT 或 TEACHER），可为 null
     * @return 登录结果（包含 token 和用户信息）
     */
    public Map<String, Object> loginWithRole(String username, String password, String role) {
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证角色是否匹配（如果指定了角色）
        if (role != null && !role.isEmpty()) {
            String userRole = user.getRole();
            if (userRole == null || !userRole.equalsIgnoreCase(role)) {
                String roleName = "TEACHER".equalsIgnoreCase(role) ? "教师" : "学生";
                throw new RuntimeException("该账号不是" + roleName + "身份，请切换身份后登录");
            }
        }

        // 简单密码验证（生产环境建议使用 BCrypt 加密）
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        // 生成 token
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, user);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    /**
     * 验证 token 是否有效
     */
    public static User validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return tokenStore.get(token);
    }

    @Override
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            tokenStore.remove(token);
        }
    }

    // ===== 学生相关 =====

    @Override
    public String createStudent(Student student) {
        if (student == null || student.getId() == null || student.getId().isEmpty()) {
            throw new RuntimeException("学生ID不能为空");
        }
        studentRepository.save(student);
        return "创建成功";
    }

    @Override
    public Student getStudentById(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            throw new RuntimeException("学生ID不能为空");
        }
        return studentRepository.findById(studentId).orElse(null);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public String updateStudent(Student student) {
        if (student == null || student.getId() == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        if (!studentRepository.existsById(student.getId())) {
            throw new RuntimeException("学生不存在");
        }
        studentRepository.save(student);
        return "更新成功";
    }

    @Override
    public String deleteStudentById(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            throw new RuntimeException("学生ID不能为空");
        }
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("学生不存在");
        }
        studentRepository.deleteById(studentId);
        return "删除成功";
    }
}
