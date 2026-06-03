package com.example.attendancesystem.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "[user]")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    @JsonProperty("user_name")
    private String username;

    @Column(name = "password", nullable = false)
    @JsonProperty("pass_word")
    private String password;

    @Column(name = "real_name")
    @JsonProperty("real_name")
    private String realName;

    @Column(name = "role")
    private String role;

    @Column(name = "create_time")
    @JsonProperty("create_time")
    private LocalDateTime createTime;
}
