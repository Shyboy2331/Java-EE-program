package com.example.attendancesystem.servise;

import com.example.attendancesystem.data.User;

import java.util.List;

public interface StudentServise {
    // 新增用户
    String createUser(User user);

    // 根据ID查询
    User getUserById(Long userId);

    // 根据用户名查询
    User getUserByUsername(String username);

    // 查询所有教师
    List<User> getAllTeachers();

    // 查询所有用户
    List<User> getAllUsers();

    // 更新用户
    String updateUser(User user);

    // 删除用户
    String deleteUserById(Long userId);
}
