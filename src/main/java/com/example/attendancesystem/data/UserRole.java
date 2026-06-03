package com.example.attendancesystem.data;

/**
 * 用户角色枚举
 */
public enum UserRole {
    /**
     * 学生
     */
    STUDENT("学生"),
    
    /**
     * 教师
     */
    TEACHER("教师");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 从字符串转换为 UserRole 枚举
     */
    public static UserRole fromString(String role) {
        if (role == null || role.isEmpty()) {
            return STUDENT;
        }
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STUDENT;
        }
    }
}
