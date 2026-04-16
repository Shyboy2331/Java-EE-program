package com.example.attendancesystem.Dao;

import com.example.attendancesystem.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {
    private final JdbcTemplate jdbcTemplate;

    // 使用构造函数注入是 Spring 官方推荐的最佳实践
    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 新增教师用户
    public void insert(User user) {
        // SQL Server 中 user 是关键字，必须用 [] 包裹
        String sql = "INSERT INTO [user] (username, password, real_name, role) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getRealName(), user.getRole());
    }

    // 根据ID查询
    public User findById(Long id) {
        String sql = "SELECT * FROM [user] WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
        } catch (EmptyResultDataAccessException e) {
            return null; // 防止找不到数据时直接抛出异常导致程序崩溃
        }
    }

    // 根据用户名查询
    public User findByUsername(String username) {
        String sql = "SELECT * FROM [user] WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 查询所有教师
    public List<User> findAllTeachers() {
        String sql = "SELECT * FROM [user] WHERE role = 'TEACHER'";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    // 查询所有用户
    public List<User> findAll() {
        String sql = "SELECT * FROM [user]";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    // 更新
    public void update(User user) {
        String sql = "UPDATE [user] SET password = ?, real_name = ?, role = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getPassword(), user.getRealName(), user.getRole(), user.getId());
    }

    // 删除
    public void deleteById(Long id) {
        String sql = "DELETE FROM [user] WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}