package com.example.attendancesystem.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T>
{
    private Integer code;
    private String mes;
    private T data;

    public static <T> Result<T> success(T data){
        return new Result<>(200,"操作成功",data);
    }
    public static <T> Result<T> error(String mes){
        return new Result<>(500,mes,null);
    }
}