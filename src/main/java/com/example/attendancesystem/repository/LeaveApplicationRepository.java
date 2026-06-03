package com.example.attendancesystem.repository;

import com.example.attendancesystem.data.LeaveApplication;
import com.example.attendancesystem.data.LeaveApplication.LeaveStatus;
import com.example.attendancesystem.data.LeaveApplication.CancelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 请假申请数据访问层
 */
@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

    /**
     * 根据学号查询请假申请列表
     */
    List<LeaveApplication> findByStudentIdOrderByCreateTimeDesc(String studentId);

    /**
     * 根据学生姓名查询请假申请列表
     */
    List<LeaveApplication> findByStudentNameOrderByCreateTimeDesc(String studentName);

    /**
     * 根据审批状态查询请假申请列表
     */
    List<LeaveApplication> findByStatusOrderByCreateTimeDesc(LeaveStatus status);

    /**
     * 根据学号和审批状态查询
     */
    List<LeaveApplication> findByStudentIdAndStatusOrderByCreateTimeDesc(String studentId, LeaveStatus status);

    /**
     * 查询所有待审批的请假申请
     */
    List<LeaveApplication> findByStatusOrderByCreateTimeDesc(LeaveStatus status);

    /**
     * 查询需要销假的学生（已批准且未销假，且结束时间已过）
     */
    @Query("SELECT l FROM LeaveApplication l WHERE l.status = :status AND l.cancelStatus = :cancelStatus AND l.endTime < :now")
    List<LeaveApplication> findNeedCancelLeave(@Param("status") LeaveStatus status, 
                                                @Param("cancelStatus") CancelStatus cancelStatus, 
                                                @Param("now") LocalDateTime now);

    /**
     * 根据学号查询待审批的申请
     */
    @Query("SELECT l FROM LeaveApplication l WHERE l.studentId = :studentId AND l.status = :status")
    List<LeaveApplication> findPendingByStudentId(@Param("studentId") String studentId, 
                                                   @Param("status") LeaveStatus status);
}
