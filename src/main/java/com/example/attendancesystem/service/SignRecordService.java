package com.example.attendancesystem.service;

import com.example.attendancesystem.data.Course;
import com.example.attendancesystem.data.SignRecord;
import com.example.attendancesystem.data.StudentCourse;
import com.example.attendancesystem.repository.CourseRepository;
import com.example.attendancesystem.repository.SignRecordRepository;
import com.example.attendancesystem.repository.StudentCourseRepository;
import com.example.attendancesystem.signin.Sign_in;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 打卡记录服务层
 */
@Service
public class SignRecordService {

    @Autowired
    private Sign_in signIn;

    @Autowired
    private SignRecordRepository signRecordRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * 学生打卡
     * @param studentId 学生学号
     * @param studentName 学生姓名
     * @param courseId 课程 ID
     * @param classId 班级 ID
     * @param remark 备注
     * @return 打卡记录
     */
    public SignRecord sign(String studentId, String studentName, String courseId, String classId, String remark) {
        // 获取课程信息
        Course course = courseRepository.findById(Long.parseLong(courseId))
            .orElseThrow(() -> new RuntimeException("课程不存在"));

        // 获取学生选课信息
        List<StudentCourse> studentCourses = studentCourseRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (studentCourses.isEmpty()) {
            throw new RuntimeException("您未选择该课程，请先选课");
        }

        // 获取班级信息
        StudentCourse studentCourse = studentCourses.get(0);
        String classTime = studentCourse.getClassTime();

        // 创建打卡记录
        return signIn.createSignRecord(
            studentId,
            studentName,
            courseId,
            course.getCourseName(),
            classId,
            classTime,
            remark
        );
    }

    /**
     * 获取学生的已选课程
     * @param studentId 学生学号
     * @return 选课列表
     */
    public List<StudentCourse> getStudentCourses(String studentId) {
        return studentCourseRepository.findByStudentIdOrderBySelectTimeDesc(studentId);
    }

    /**
     * 获取学生的打卡历史
     * @param studentId 学生学号
     * @return 打卡记录列表
     */
    public List<SignRecord> getStudentSignHistory(String studentId) {
        return signRecordRepository.findByStudentIdOrderBySignTimeDesc(studentId);
    }

    /**
     * 获取指定课程的打卡记录
     * @param courseId 课程 ID
     * @return 打卡记录列表
     */
    public List<SignRecord> getCourseSignRecords(String courseId) {
        return signRecordRepository.findByCourseIdOrderBySignTimeDesc(courseId);
    }

    /**
     * 获取所有打卡记录
     * @return 打卡记录列表
     */
    public List<SignRecord> findAllSignRecords() {
        return signRecordRepository.findAll();
    }

    /**
     * 统计学生今日打卡次数
     * @param studentId 学生学号
     * @return 打卡次数
     */
    public long countTodaySigns(String studentId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return signIn.countStudentSigns(studentId, startOfDay, endOfDay);
    }

    /**
     * 统计学生本月打卡次数
     * @param studentId 学生学号
     * @return 打卡次数
     */
    public long countMonthSigns(String studentId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return signIn.countStudentSigns(studentId, startOfMonth, endOfMonth);
    }

    /**
     * 统计学生今日正常打卡次数
     * @param studentId 学生学号
     * @return 正常打卡次数
     */
    public long countTodayNormalSigns(String studentId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return signIn.countStudentNormalSigns(studentId, startOfDay, endOfDay);
    }

    /**
     * 创建课程
     * @param courseName 课程名称
     * @param classCount 班级个数
     * @param classTime 上课时间
     * @param teacherId 教师 ID
     * @param teacherName 教师姓名
     * @return 创建的课程
     */
    public Course createCourse(String courseName, Integer classCount, String classTime,
                               Long teacherId, String teacherName) {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setClassCount(classCount);
        course.setClassTime(classTime);
        course.setTeacherId(teacherId);
        course.setTeacherName(teacherName);
        course.setCreateTime(LocalDateTime.now());
        return courseRepository.save(course);
    }

    /**
     * 学生选课
     * @param studentId 学生学号
     * @param studentName 学生姓名
     * @param courseId 课程 ID
     * @param classId 班级 ID
     * @param classTime 上课时间
     * @return 选课记录
     */
    public StudentCourse selectCourse(String studentId, String studentName, String courseId, 
                                      String classId, String classTime) {
        // 检查是否已选
        List<StudentCourse> existing = studentCourseRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (!existing.isEmpty()) {
            throw new RuntimeException("您已选择该课程");
        }

        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(studentId);
        studentCourse.setStudentName(studentName);
        studentCourse.setCourseId(courseId);
        
        // 获取课程名称
        Course course = courseRepository.findById(Long.parseLong(courseId))
            .orElseThrow(() -> new RuntimeException("课程不存在"));
        studentCourse.setCourseName(course.getCourseName());
        
        studentCourse.setClassId(classId);
        studentCourse.setClassTime(classTime);
        studentCourse.setSelectTime(LocalDateTime.now());
        studentCourse.setCreateTime(LocalDateTime.now());
        
        return studentCourseRepository.save(studentCourse);
    }
}
