package com.example.attendancesystem.data;

public class Result<T>
{
    private Integer code;
    private String mes;
    private T data;

    public Result() {}

    public Result(Integer code, String mes, T data) {
        this.code = code;
        this.mes = mes;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return mes;
    }

    public void setMessage(String mes) {
        this.mes = mes;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Result<T> success(T data){
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    
    public static <T> Result<T> error(String mes){
        Result<T> result = new Result<T>();
        result.setCode(500);
        result.setMessage(mes);
        result.setData(null);
        return result;
    }
}
