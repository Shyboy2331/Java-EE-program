package com.example.attendancesystem.servise;

import com.example.attendancesystem.Dao.StudentDao;
import com.example.attendancesystem.data.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiseImpl implements StudentServise{
    @Autowired
    private StudentDao studentDao;

    @Override
    public String createStudent(Student student) {
        if (student.getName()==null||student.getId().isEmpty()){
            throw new RuntimeException("姓名不能为空");
        }
        studentDao.insert(student);
        return "创建成功";
    }
    @Override
    public Student getStudentById(String id) {
        return studentDao.findById(id);
    }
}
