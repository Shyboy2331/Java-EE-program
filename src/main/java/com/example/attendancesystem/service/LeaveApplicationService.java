package com.example.attendancesystem.service;

import com.example.attendancesystem.data.LeaveApplication;
import com.example.attendancesystem.data.LeaveApplication.LeaveStatus;
import com.example.attendancesystem.data.LeaveApplication.CancelStatus;
import com.example.attendancesystem.repository.LeaveApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 请假申请服务层
 */
@Service
public class LeaveApplicationService {

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    /**
     * 提交请假申请
     */
    @Transactional
    public LeaveApplication applyLeave(LeaveApplication application) {
        // 计算请假天数
        if (application.getStartTime() != null && application.getEndTime() != null) {
            Duration duration = Duration.between(application.getStartTime(), application.getEndTime());
            double days = duration.toHours() / 24.0;
            if (days < 1) {
                days = Math.ceil(duration.toMinutes() / 60.0) / 24.0;
            }
            application.setDays(days);
        }
        
        application.setStatus(LeaveStatus.PENDING);
        application.setCancelStatus(CancelStatus.NO);
        application.setCreateTime(LocalDateTime.now());
        
        return leaveApplicationRepository.save(application);
    }

    /**
     * 根据 ID 获取请假申请
     */
    public LeaveApplication getById(Long id) {
        return leaveApplicationRepository.findById(id).orElse(null);
    }

    /**
     * 根据学号获取请假申请列表
     */
    public List<LeaveApplication> getByStudentId(String studentId) {
        return leaveApplicationRepository.findByStudentIdOrderByCreateTimeDesc(studentId);
    }

    /**
     * 获取所有待审批的请假申请
     */
    public List<LeaveApplication> getPendingApplications() {
        return leaveApplicationRepository.findByStatusOrderByCreateTimeDesc(LeaveStatus.PENDING);
    }

    /**
     * 获取所有请假申请
     */
    public List<LeaveApplication> getAllApplications() {
        return leaveApplicationRepository.findAll();
    }

    /**
     * 审批请假申请（通过）
     */
    @Transactional
    public LeaveApplication approve(Long id, String approverId, String approverName, String comment) {
        LeaveApplication application = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("请假申请不存在"));
        
        application.setStatus(LeaveStatus.APPROVED);
        application.setApproverId(approverId);
        application.setApproverName(approverName);
        application.setApprovalComment(comment);
        application.setApprovalTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        
        return leaveApplicationRepository.save(application);
    }

    /**
     * 审批请假申请（驳回）
     */
    @Transactional
    public LeaveApplication reject(Long id, String approverId, String approverName, String comment) {
        LeaveApplication application = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("请假申请不存在"));
        
        application.setStatus(LeaveStatus.REJECTED);
        application.setApproverId(approverId);
        application.setApproverName(approverName);
        application.setApprovalComment(comment);
        application.setApprovalTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        
        return leaveApplicationRepository.save(application);
    }

    /**
     * 销假
     */
    @Transactional
    public LeaveApplication cancelLeave(Long id) {
        LeaveApplication application = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("请假申请不存在"));
        
        if (application.getStatus() != LeaveStatus.APPROVED) {
            throw new RuntimeException("只有已批准的请假申请才能销假");
        }
        
        application.setCancelStatus(CancelStatus.YES);
        application.setCancelTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        
        return leaveApplicationRepository.save(application);
    }

    /**
     * 获取需要销假的学生列表（已批准、未销假、结束时间已过）
     */
    public List<LeaveApplication> getNeedCancelLeave() {
        return leaveApplicationRepository.findNeedCancelLeave(
                LeaveStatus.APPROVED, 
                CancelStatus.NO, 
                LocalDateTime.now()
        );
    }

    /**
     * 获取已批准的请假申请
     */
    public List<LeaveApplication> getApprovedApplications() {
        return leaveApplicationRepository.findByStatusOrderByCreateTimeDesc(LeaveStatus.APPROVED);
    }

    /**
     * 获取已驳回的请假申请
     */
    public List<LeaveApplication> getRejectedApplications() {
        return leaveApplicationRepository.findByStatusOrderByCreateTimeDesc(LeaveStatus.REJECTED);
    }

    /**
     * 检查学生是否有待审批的申请
     */
    public boolean hasPendingApplication(String studentId) {
        List<LeaveApplication> pending = leaveApplicationRepository.findPendingByStudentId(
                studentId, LeaveStatus.PENDING);
        return !pending.isEmpty();
    }
}
