package com.example.attendancesystem.controller;

import com.example.attendancesystem.data.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据管理控制器
 */
@RestController
@RequestMapping("/api/admin")
public class DataAdminController {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 清除所有业务数据
     * POST /api/admin/clear-all-data
     */
    @PostMapping("/clear-all-data")
    @Transactional
    public Result<String> clearAllData() {
        try {
            System.out.println("开始清除所有业务数据...");

            // 1. 删除打卡记录
            entityManager.createNativeQuery("DELETE FROM sign_record").executeUpdate();
            System.out.println("已删除打卡记录");

            // 2. 删除学生选课记录
            entityManager.createNativeQuery("DELETE FROM student_course").executeUpdate();
            System.out.println("已删除学生选课记录");

            // 3. 删除课程
            entityManager.createNativeQuery("DELETE FROM course").executeUpdate();
            System.out.println("已删除课程");

            // 4. 删除学生
            entityManager.createNativeQuery("DELETE FROM student").executeUpdate();
            System.out.println("已删除学生");

            // 5. 删除用户
            entityManager.createNativeQuery("DELETE FROM [user]").executeUpdate();
            System.out.println("已删除用户");

            System.out.println("所有业务数据清除完成！");
            return Result.success("所有业务数据清除完成！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("清除数据失败：" + e.getMessage());
        }
    }
}
