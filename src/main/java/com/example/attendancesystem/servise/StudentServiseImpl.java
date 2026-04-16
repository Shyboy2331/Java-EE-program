package com.example.attendancesystem.servise;

import com.example.attendancesystem.Dao.UserDao;
import com.example.attendancesystem.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiseImpl implements StudentServise {

    @Autowired
    private UserDao userDao;

    @Override
    public String createUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        userDao.insert(user);
        return "创建成功";
    }

    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return userDao.findById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        return userDao.findByUsername(username);
    }

    @Override
    public List<User> getAllTeachers() {
        return userDao.findAllTeachers();
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public String updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        User existingUser = userDao.findById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        userDao.update(user);
        return "更新成功";
    }

    @Override
    public String deleteUserById(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        User existingUser = userDao.findById(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        userDao.deleteById(userId);
        return "删除成功";
    }
}
