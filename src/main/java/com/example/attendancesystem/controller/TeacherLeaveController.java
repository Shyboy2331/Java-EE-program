package com.example.attendancesystem.controller;

import com.example.attendancesystem.data.LeaveApplication;
import com.example.attendancesystem.data.LeaveApplication.LeaveStatus;
import com.example.attendancesystem.data.LeaveApplication.CancelStatus;
import com.example.attendancesystem.data.Result;
import com.example.attendancesystem.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师请假管理控制器
 */
@RestController
@RequestMapping("/teacher/api/leave")
public class TeacherLeaveController {

    @Autowired
    private LeaveApplicationService leaveApplicationService;

    /**
     * 获取所有待审批的请假申请
     * GET /teacher/api/leave/pending
     */
    @GetMapping("/pending")
    public Result<List<LeaveApplication>> getPendingApplications() {
        try {
            List<LeaveApplication> list = leaveApplicationService.getPendingApplications();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有请假申请
     * GET /teacher/api/leave/all
     */
    @GetMapping("/all")
    public Result<List<LeaveApplication>> getAllApplications() {
        try {
            List<LeaveApplication> list = leaveApplicationService.getAllApplications();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取已批准的请假申请
     * GET /teacher/api/leave/approved
     */
    @GetMapping("/approved")
    public Result<List<LeaveApplication>> getApprovedApplications() {
        try {
            List<LeaveApplication> list = leaveApplicationService.getApprovedApplications();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取已驳回的请假申请
     * GET /teacher/api/leave/rejected
     */
    @GetMapping("/rejected")
    public Result<List<LeaveApplication>> getRejectedApplications() {
        try {
            List<LeaveApplication> list = leaveApplicationService.getRejectedApplications();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取需要销假的学生列表
     * GET /teacher/api/leave/need-cancel
     */
    @GetMapping("/need-cancel")
    public Result<List<LeaveApplication>> getNeedCancelLeave() {
        try {
            List<LeaveApplication> list = leaveApplicationService.getNeedCancelLeave();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 审批通过
     * POST /teacher/api/leave/approve/{id}
     */
    @PostMapping("/approve/{id}")
    public Result<LeaveApplication> approveApplication(
            @PathVariable Long id,
            @RequestParam String approverId,
            @RequestParam String approverName,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String comment = body != null ? body.get("comment") : "";
            LeaveApplication application = leaveApplicationService.approve(id, approverId, approverName, comment);
            return Result.success(application, "审批通过");
        } catch (Exception e) {
            return Result.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 审批驳回
     * POST /teacher/api/leave/reject/{id}
     */
    @PostMapping("/reject/{id}")
    public Result<LeaveApplication> rejectApplication(
            @PathVariable Long id,
            @RequestParam String approverId,
            @RequestParam String approverName,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String comment = body != null ? body.get("comment") : "";
            LeaveApplication application = leaveApplicationService.reject(id, approverId, approverName, comment);
            return Result.success(application, "已驳回");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 获取请假申请详情
     * GET /teacher/api/leave/detail/{id}
     */
    @GetMapping("/detail/{id}")
    public Result<LeaveApplication> getLeaveDetail(@PathVariable Long id) {
        try {
            LeaveApplication application = leaveApplicationService.getById(id);
            if (application == null) {
                return Result.error("请假记录不存在");
            }
            return Result.success(application);
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取统计数据
     * GET /teacher/api/leave/stats
     */
    @GetMapping("/stats")
    public Result<Map<String, Integer>> getStats() {
        try {
            List<LeaveApplication> all = leaveApplicationService.getAllApplications();
            List<LeaveApplication> pending = leaveApplicationService.getPendingApplications();
            List<LeaveApplication> approved = leaveApplicationService.getApprovedApplications();
            List<LeaveApplication> rejected = leaveApplicationService.getRejectedApplications();
            List<LeaveApplication> needCancel = leaveApplicationService.getNeedCancelLeave();

            Map<String, Integer> stats = new HashMap<>();
            stats.put("total", all.size());
            stats.put("pending", pending.size());
            stats.put("approved", approved.size());
            stats.put("rejected", rejected.size());
            stats.put("needCancel", needCancel.size());

            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计失败：" + e.getMessage());
        }
    }
}
