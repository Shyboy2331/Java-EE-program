package com.example.attendancesystem.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @JsonProperty("student_id")
    private String id;

    @JsonProperty("student_name")
    private String name;

    @JsonProperty("class_name")
    private String classname;
}
