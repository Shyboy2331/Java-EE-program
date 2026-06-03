package com.example.attendancesystem.repository;

import com.example.attendancesystem.data.SignRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SignRecordRepository extends JpaRepository<SignRecord, Long> {
    
    /**
     * 根据学生 ID 查询打卡记录
     */
    List<SignRecord> findByStudentIdOrderBySignTimeDesc(String studentId);
    
    /**
     * 根据课程 ID 查询打卡记录
     */
    List<SignRecord> findByCourseIdOrderBySignTimeDesc(String courseId);
    
    /**
     * 根据学生 ID 和课程 ID 查询打卡记录
     */
    List<SignRecord> findByStudentIdAndCourseIdOrderBySignTimeDesc(String studentId, String courseId);
    
    /**
     * 根据学生 ID 和日期范围查询打卡记录
     */
    List<SignRecord> findByStudentIdAndSignTimeBetween(String studentId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计指定日期范围内的打卡记录数
     */
    long countByStudentIdAndSignTimeBetween(String studentId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计指定日期范围内正常打卡的记录数
     */
    long countByStudentIdAndStatusAndSignTimeBetween(String studentId, Integer status, LocalDateTime startTime, LocalDateTime endTime);
}
