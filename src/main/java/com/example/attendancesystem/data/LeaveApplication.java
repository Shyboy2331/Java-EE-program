package com.example.attendancesystem.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 请假申请实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "leave_applications")
public class LeaveApplication {

    /**
     * 请假申请 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学生学号
     */
    @Column(name = "student_id", nullable = false, length = 20)
    private String studentId;

    /**
     * 学生姓名
     */
    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

    /**
     * 请假类型：病假、事假、公假、其他
     */
    @Column(name = "leave_type", nullable = false, length = 20)
    private String leaveType;

    /**
     * 开始时间
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * 课程 ID（可选）
     */
    @Column(name = "course_id", length = 20)
    private String courseId;

    /**
     * 课程名称（可选）
     */
    @Column(name = "course_name", length = 100)
    private String courseName;

    /**
     * 请假天数
     */
    @Column(name = "days")
    private Double days;

    /**
     * 请假事由
     */
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    /**
     * 家属是否知情：0-否，1-是
     */
    @Column(name = "family_known", nullable = false)
    private Integer familyKnown;

    /**
     * 家属联系方式
     */
    @Column(name = "family_contact", length = 20)
    private String familyContact;

    /**
     * 学生联系方式
     */
    @Column(name = "student_contact", length = 20)
    private String studentContact;

    /**
     * 审批状态：PENDING-审批中，APPROVED-通过，REJECTED-驳回
     */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.PENDING;

    /**
     * 审批意见
     */
    @Column(name = "approval_comment", length = 500)
    private String approvalComment;

    /**
     * 审批人 ID（教师 ID）
     */
    @Column(name = "approver_id", length = 20)
    private String approverId;

    /**
     * 审批人姓名
     */
    @Column(name = "approver_name", length = 50)
    private String approverName;

    /**
     * 审批时间
     */
    @Column(name = "approval_time")
    private LocalDateTime approvalTime;

    /**
     * 销假状态：NO-未销假，YES-已销假
     */
    @Column(name = "cancel_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CancelStatus cancelStatus = CancelStatus.NO;

    /**
     * 销假时间
     */
    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    /**
     * 申请时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 请假状态枚举
     */
    public enum LeaveStatus {
        PENDING("审批中"),
        APPROVED("通过"),
        REJECTED("驳回");

        private final String description;

        LeaveStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 销假状态枚举
     */
    public enum CancelStatus {
        NO("未销假"),
        YES("已销假");

        private final String description;

        CancelStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
