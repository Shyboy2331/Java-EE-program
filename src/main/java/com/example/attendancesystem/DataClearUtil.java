package com.example.attendancesystem;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据清除工具
 * 用于清除所有业务数据
 */
@Component
public class DataClearUtil implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 是否启用自动清除数据
     * 设置为 true 时，应用启动后会自动清除所有业务数据
     */
    private static final boolean ENABLE_AUTO_CLEAR = false;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (ENABLE_AUTO_CLEAR) {
            clearAllData();
        }
    }

    /**
     * 清除所有业务数据
     * 按照外键依赖顺序删除
     */
    @Transactional
    public void clearAllData() {
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
    }
}
