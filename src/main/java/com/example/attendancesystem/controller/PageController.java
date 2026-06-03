package com.example.attendancesystem.controller;

import com.example.attendancesystem.data.Student;
import com.example.attendancesystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 页面跳转控制器
 */
@Controller
public class PageController {

    @Autowired
    private StudentService studentService;

    /**
     * 根路径 - 根据登录状态跳转到登录页或对应首页
     * GET /
     */
    @GetMapping("/")
    public ModelAndView index() {
        // 默认跳转到登录页
        ModelAndView mv = new ModelAndView("redirect:/login");
        return mv;
    }

    /**
     * 教师主页索引
     * GET /tch-homepage
     */
    @GetMapping("/tch-homepage")
    public ModelAndView tchHomepage() {
        ModelAndView mv = new ModelAndView("redirect:/teacher/homepage");
        return mv;
    }

    /**
     * 学生主页索引
     * GET /stu-homepage
     */
    @GetMapping("/stu-homepage")
    public ModelAndView stuHomepage() {
        ModelAndView mv = new ModelAndView("redirect:/student/homepage");
        return mv;
    }

    /**
     * 教师创建课程索引
     * GET /tch-course-create
     */
    @GetMapping("/tch-course-create")
    public ModelAndView tchCourseCreate() {
        ModelAndView mv = new ModelAndView("redirect:/teacher/course/create");
        return mv;
    }

    /**
     * 学生选择课程索引
     * GET /stu-course-select
     */
    @GetMapping("/stu-course-select")
    public ModelAndView stuCourseSelect() {
        ModelAndView mv = new ModelAndView("redirect:/student/course/select");
        return mv;
    }

    /**
     * 登录页面
     * GET /login
     */
    @GetMapping("/login")
    public ModelAndView loginPage() {
        ModelAndView mv = new ModelAndView("login");
        mv.addObject("title", "用户登录");
        return mv;
    }

    /**
     * 注册页面
     * GET /register
     */
    @GetMapping("/register")
    public ModelAndView registerPage() {
        ModelAndView mv = new ModelAndView("register");
        mv.addObject("title", "用户注册");
        return mv;
    }

    /**
     * 未登录提示页面
     * GET /need-login
     */
    @GetMapping("/need-login")
    public ModelAndView needLoginPage() {
        ModelAndView mv = new ModelAndView("need-login");
        mv.addObject("title", "请先登录");
        return mv;
    }

    /**
     * 首页
     * GET /student/homepage
     */
    @GetMapping("/student/homepage")
    public ModelAndView homepage() {
        ModelAndView mv = new ModelAndView("stu-homepage");
        mv.addObject("title", "学生首页");
        mv.addObject("userRole", "STUDENT");

        // 加载统计数据
        List<Student> students = studentService.getAllStudents();
        mv.addObject("studentCount", students.size());
        mv.addObject("attendanceCount", (int) (students.size() * 0.95));
        mv.addObject("rateCount", "95%");
        mv.addObject("lateCount", (int) (students.size() * 0.05));

        // 最新 5 名学生
        List<Student> recentStudents = students.stream()
            .limit(5)
            .toList();
        mv.addObject("recentStudents", recentStudents);

        // 当前日期时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日 EEEE");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        mv.addObject("currentDate", now.format(dateFormatter));
        mv.addObject("currentTime", now.format(timeFormatter));

        return mv;
    }

    /**
     * 教师首页
     * GET /teacher/homepage
     */
    @GetMapping("/teacher/homepage")
    public ModelAndView teacherHomepage() {
        ModelAndView mv = new ModelAndView("tch-homepage");
        mv.addObject("title", "教师首页");
        mv.addObject("userRole", "TEACHER");

        // 加载统计数据
        List<Student> students = studentService.getAllStudents();
        mv.addObject("studentCount", students.size());
        mv.addObject("attendanceCount", (int) (students.size() * 0.95));
        mv.addObject("rateCount", "95%");
        mv.addObject("lateCount", (int) (students.size() * 0.05));

        // 最新 5 名学生
        List<Student> recentStudents = students.stream()
            .limit(5)
            .toList();
        mv.addObject("recentStudents", recentStudents);

        // 当前日期时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日 EEEE");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        mv.addObject("currentDate", now.format(dateFormatter));
        mv.addObject("currentTime", now.format(timeFormatter));

        return mv;
    }

    /**
     * 学生打卡页面
     * GET /student/sign-in
     */
    @GetMapping("/student/sign-in")
    public ModelAndView signInPage() {
        ModelAndView mv = new ModelAndView("sign-in");
        mv.addObject("title", "打卡签到");
        return mv;
    }

    /**
     * 学生请假申请页面
     * GET /student/ask-for-leave
     */
    @GetMapping("/student/ask-for-leave")
    public ModelAndView askForLeavePage() {
        ModelAndView mv = new ModelAndView("ask-for-leave");
        mv.addObject("title", "请假申请");
        return mv;
    }

    /**
     * 教师请假管理页面
     * GET /teacher/leave-manage
     */
    @GetMapping("/teacher/leave-manage")
    public ModelAndView teacherLeaveManagePage() {
        ModelAndView mv = new ModelAndView("teacher-leave-manage");
        mv.addObject("title", "请假管理");
        return mv;
    }

    /**
     * 学生课程选择页面
     * GET /student/course/select
     */
    @GetMapping("/student/course/select")
    public ModelAndView courseSelectPage() {
        ModelAndView mv = new ModelAndView("course-select");
        mv.addObject("title", "选择课程");
        return mv;
    }

    /**
     * 学生打卡记录页面
     * GET /student/sign-records
     */
    @GetMapping("/student/sign-records")
    public ModelAndView signRecordsPage() {
        ModelAndView mv = new ModelAndView("stu-sign-list");
        mv.addObject("title", "我的打卡记录");
        return mv;
    }

    /**
     * 教师创建课程页面
     * GET /teacher/course/create
     */
    @GetMapping("/teacher/course/create")
    public ModelAndView courseCreatePage() {
        ModelAndView mv = new ModelAndView("course-create");
        mv.addObject("title", "创建课程");
        return mv;
    }

    /**
     * 教师打卡记录页面
     * GET /tch-sign-list
     */
    @GetMapping("/tch-sign-list")
    public ModelAndView teacherSignListPage() {
        ModelAndView mv = new ModelAndView("tch-sign-list");
        mv.addObject("title", "打卡记录");
        return mv;
    }

    /**
     * 教师课程管理页面（重定向到创建课程）
     * GET /teacher/class-list
     */
    @GetMapping("/teacher/class-list")
    public ModelAndView teacherClassListRedirect() {
        ModelAndView mv = new ModelAndView("redirect:/teacher/course/create");
        return mv;
    }

    /**
     * 学生列表页面
     * GET /student/list-page
     */
    @GetMapping("/student/list-page")
    public ModelAndView listPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String classname,
            @RequestParam(required = false) String gender) {
        ModelAndView mv = new ModelAndView("stu-list");
        mv.addObject("title", "学生列表");

        // 加载学生数据
        List<Student> students = studentService.getAllStudents();

        // 前端搜索过滤
        if (name != null && !name.isEmpty()) {
            students = students.stream()
                .filter(s -> s.getName() != null && s.getName().contains(name))
                .toList();
        }
        if (classname != null && !classname.isEmpty()) {
            students = students.stream()
                .filter(s -> s.getClassname() != null && s.getClassname().contains(classname))
                .toList();
        }
        if (gender != null && !gender.isEmpty()) {
            students = students.stream()
                .filter(s -> s.getGender() != null && s.getGender().equals(gender))
                .toList();
        }

        mv.addObject("students", students);
        mv.addObject("studentCount", students.size());
        mv.addObject("paramName", name != null ? name : "");
        mv.addObject("paramClassname", classname != null ? classname : "");
        mv.addObject("paramGender", gender != null ? gender : "");

        return mv;
    }

    /**
     * 添加学生页面
     * GET /student/add
     */
    @GetMapping("/student/add")
    public ModelAndView addPage() {
        ModelAndView mv = new ModelAndView("add-stu");
        mv.addObject("title", "添加学生");
        return mv;
    }

    /**
     * 编辑学生页面
     * GET /student/edit
     */
    @GetMapping("/student/edit")
    public ModelAndView editPage(@RequestParam(required = false) String id) {
        ModelAndView mv = new ModelAndView("edit-stu");
        mv.addObject("title", "编辑学生");
        mv.addObject("studentId", id);

        // 如果提供了 ID，加载学生数据
        if (id != null && !id.isEmpty()) {
            Student student = studentService.getStudentById(id);
            mv.addObject("student", student);
        }

        return mv;
    }

    /**
     * 文件导入页面
     * GET /file-import
     */
    @GetMapping("/file-import")
    public ModelAndView fileImportPage() {
        ModelAndView mv = new ModelAndView("file-import");
        mv.addObject("title", "文件导入");
        return mv;
    }
}
