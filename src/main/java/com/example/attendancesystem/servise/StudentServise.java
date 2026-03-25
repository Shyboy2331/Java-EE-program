package com.example.attendancesystem.servise;

import com.example.attendancesystem.Dao.StudentDao;
import com.example.attendancesystem.data.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface StudentServise {
    String createStudent(Student student);
    Student getStudentById(String studentId);
}

