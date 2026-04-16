package com.example.attendancesystem.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;

    @JsonProperty("user_name")
    private String username;

    @JsonProperty("pass_word")
    private String password;

    @JsonProperty("real_name")
    private String realName;

    private String role;

    @JsonProperty("create_time")
    private LocalDateTime createTime;
}
