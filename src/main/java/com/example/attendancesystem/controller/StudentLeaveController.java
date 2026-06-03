package com.example.attendancesystem.controller;

import com.example.attendancesystem.data.LeaveApplication;
import com.example.attendancesystem.data.LeaveApplication.LeaveStatus;
import com.example.attendancesystem.data.Result;
import com.example.attendancesystem.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生请假控制器
 */
@RestController
@RequestMapping("/student/api/leave")
public class StudentLeaveController {

    @Autowired
    private LeaveApplicationService leaveApplicationService;

    /**
     * 提交请假申请
     * POST /student/api/leave/apply
     */
    @PostMapping("/apply")
    public Result<LeaveApplication> applyLeave(@RequestBody LeaveApplication application) {
        try {
            // 验证必填字段
            if (application.getStudentId() == null || application.getStudentId().isEmpty()) {
                return Result.error("学号不能为空");
            }
            if (application.getStudentName() == null || application.getStudentName().isEmpty()) {
                return Result.error("姓名不能为空");
            }
            if (application.getLeaveType() == null || application.getLeaveType().isEmpty()) {
                return Result.error("请假类型不能为空");
            }
            if (application.getStartTime() == null) {
                return Result.error("开始时间不能为空");
            }
            if (application.getEndTime() == null) {
                return Result.error("结束时间不能为空");
            }
            if (application.getEndTime().isBefore(application.getStartTime())) {
                return Result.error("结束时间必须晚于开始时间");
            }
            if (application.getReason() == null || application.getReason().isEmpty()) {
                return Result.error("请假事由不能为空");
            }
            if (application.getFamilyKnown() == null) {
                return Result.error("请选择家属是否知情");
            }

            LeaveApplication saved = leaveApplicationService.applyLeave(application);
            return Result.success(saved, "申请提交成功");
        } catch (Exception e) {
            return Result.error("提交失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前学生的请假记录列表
     * GET /student/api/leave/list
     */
    @GetMapping("/list")
    public Result<List<LeaveApplication>> getLeaveList(@RequestParam String studentId) {
        try {
            List<LeaveApplication> list = leaveApplicationService.getByStudentId(studentId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 销假
     * POST /student/api/leave/cancel/{id}
     */
    @PostMapping("/cancel/{id}")
    public Result<LeaveApplication> cancelLeave(@PathVariable Long id) {
        try {
            LeaveApplication application = leaveApplicationService.getById(id);
            if (application == null) {
                return Result.error("请假记录不存在");
            }
            if (application.getStatus() != LeaveStatus.APPROVED) {
                return Result.error("只有已批准的请假申请才能销假");
            }
            LeaveApplication updated = leaveApplicationService.cancelLeave(id);
            return Result.success(updated, "销假成功");
        } catch (Exception e) {
            return Result.error("销假失败：" + e.getMessage());
        }
    }

    /**
     * 获取请假记录详情
     * GET /student/api/leave/detail/{id}
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
     * 检查是否有待审批的申请
     * GET /student/api/leave/has-pending
     */
    @GetMapping("/has-pending")
    public Result<Map<String, Boolean>> hasPendingApplication(@RequestParam String studentId) {
        try {
            boolean hasPending = leaveApplicationService.hasPendingApplication(studentId);
            Map<String, Boolean> data = new HashMap<>();
            data.put("hasPending", hasPending);
            return Result.success(data);
        } catch (Exception e) {
            Map<String, Boolean> data = new HashMap<>();
            data.put("hasPending", false);
            return Result.success(data);
        }
    }
}
