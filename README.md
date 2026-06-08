# 考勤管理系统 (Attendance Management System)

基于 Spring Boot 4.x 的学生考勤管理系统，提供完整的考勤管理、课程管理、请假管理等功能。

## 项目作者

西南财经大学 计算机科学与技术 计科3班 黄巍然 42411140

## 技术栈

### 后端
- **Java 21**
- **Spring Boot 4.0.3**
- **Spring MVC** - Web 框架
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据访问
- **Thymeleaf** - 模板引擎
- **Apache POI 5.2.5** - Excel 文件处理

### 前端
- **HTML5/CSS3**
- **JavaScript (ES6+)**
- **Chart.js 4.4.0** - 数据可视化

### 数据库
- **SQL Server** - 关系型数据库
- **Hibernate** - ORM 框架

## 项目结构

```
attendancesystem/
├── src/main/java/com/example/attendancesystem/
│   ├── config/                    # 配置类
│   │   ├── SecurityConfig.java    # Spring Security 配置
│   │   └── WebConfig.java         # Web MVC 和 CORS 配置
│   ├── controller/                # 控制器层
│   │   ├── AttendanceStatsController.java
│   │   ├── ClassController.java
│   │   ├── CourseController.java
│   │   ├── DataAdminController.java
│   │   ├── FileImportController.java
│   │   ├── PageController.java
│   │   ├── SignRecordController.java
│   │   ├── StudentController.java
│   │   ├── StudentCourseController.java
│   │   ├── StudentLeaveController.java
│   │   ├── TeacherLeaveController.java
│   │   └── UserController.java
│   ├── data/                      # 实体类和数据传输对象
│   │   ├── Course.java
│   │   ├── LeaveApplication.java
│   │   ├── SignRecord.java
│   │   ├── Student.java
│   │   ├── User.java
│   │   └── ...
│   ├── interceptor/               # 拦截器
│   │   ├── LoginInterceptor.java
│   │   └── PageLoginInterceptor.java
│   ├── repository/                # 数据访问层
│   │   ├── CourseRepository.java
│   │   ├── SignRecordRepository.java
│   │   ├── StudentRepository.java
│   │   └── ...
│   ├── service/                   # 服务层
│   │   ├── AttendanceStatsService.java
│   │   ├── SignRecordService.java
│   │   ├── StudentService.java
│   │   └── impl/
│   └── AttendanceSystemApplication.java  # 主启动类
├── src/main/resources/
│   ├── templates/                 # Thymeleaf 模板
│   │   ├── login.html
│   │   ├── register.html
│   │   ├── stu-homepage.html
│   │   ├── tch-homepage.html
│   │   ├── sign-in.html
│   │   ├── course-create.html
│   │   ├── file-import.html
│   │   └── ...
│   └── application.yml            # 应用配置
└── pom.xml                        # Maven 配置
```

## 功能模块

### 用户管理
- **用户注册** - 支持学生和教师两种身份注册
- **用户登录** - 基于 Spring Security 的认证系统
- **权限控制** - 根据角色（学生/教师）访问不同功能

### 教师端功能
- **主页** - 考勤统计概览（表格/图表）
- **课程管理** - 创建课程、自动生成班级
- **学生管理** - 学生列表、批量导入
- **打卡记录** - 查看学生打卡情况、导出 Excel
- **文件导入** - 批量导入学生数据和打卡记录
- **请假管理** - 审批学生请假申请

### 学生端功能
- **主页** - 个人考勤记录查询
- **打卡签到** - 每日打卡（支持迟到判断）
- **课程选择** - 选择已创建的课程班级
- **请假申请** - 提交请假申请
- **打卡记录** - 查询个人历史打卡记录

### 数据统计
- **出勤率统计** - 按周/月统计，支持图表可视化
- **班级出勤统计** - 各班级出勤情况对比
- **个人考勤统计** - 学生个人出勤情况分析

## 快速开始

### 环境要求
- JDK 21+
- Maven 3.9+
- SQL Server（推荐 SQL Server Express）

### 数据库配置
1. 创建数据库 `studentinfo`
2. 修改 `application.yml` 中的数据库连接配置

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=studentinfo;integratedSecurity=true
```

### 运行项目

```bash
# 使用 Maven Wrapper
./mvnw spring-boot:run

# 或使用系统 Maven
mvn spring-boot:run
```

应用启动后访问：http://localhost:8080

### 打包部署

```bash
# 打包
mvn package

# 运行 jar
java -jar target/attendancesystem-0.0.1-SNAPSHOT.jar
```

## API 接口

### 用户相关
| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/create` | POST | 用户注册 |
| `/student/api/login` | POST | 学生登录 |
| `/teacher/api/login` | POST | 教师登录 |

### 课程相关
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/course/create` | POST | 创建课程 |
| `/api/course/teacher` | GET | 获取教师课程列表 |
| `/api/class/generate` | POST | 生成班级列表 |

### 打卡相关
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/sign-in` | POST | 学生打卡 |
| `/api/sign-records` | GET | 查询打卡记录 |
| `/student/api/sign/export-excel` | GET | 导出打卡记录 Excel |

### 文件导入
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/import/students` | POST | 导入学生数据 |
| `/api/import/sign-records` | POST | 导入打卡记录 |

## 核心特性

### 安全认证
- Spring Security 配置的角色权限控制
- 页面访问拦截（LoginInterceptor）
- API 接口 Token 验证

### 数据导入
- 支持 Excel (.xls, .xlsx) 格式
- 批量导入学生和打卡记录
- 导入结果反馈和错误报告

### 数据导出
- 打卡记录导出为 Excel
- 支持状态颜色标识（正常/迟到）
- 包含统计摘要

### 考勤统计
- 多维度统计（按周/月/班级）
- Chart.js 图表可视化
- 出勤率等级标识（优秀/一般/较差）

## 开发信息

### 项目信息
- **项目名称**: attendancesystem
- **版本**: 0.0.1-SNAPSHOT
- **Java 版本**: 21
- **作者**: 黄巍然
- **专业**: 计算机科学与技术

### 构建配置
```xml
<properties>
    <java.version>21</java.version>
</properties>
```

### 主要依赖
- spring-boot-starter-webmvc
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-thymeleaf
- poi-ooxml (Excel 处理)
- mssql-jdbc (SQL Server 驱动)

## 常见问题

### 数据库连接失败
确保 SQL Server 服务已启动，并检查 `application.yml` 中的连接配置。

### CORS 错误
项目已配置 CORS，允许跨域请求。如遇问题，检查 `WebConfig.java` 中的 `addCorsMappings` 配置。

### 文件上传失败
检查 `application.yml` 中的文件上传大小限制配置。

## License

本项目仅供学习参考使用。
