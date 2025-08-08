package com.example.JobApp.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ApiResponse<T> {
    public int statusCode;
    public String message;
    public boolean success;
    public T data;


    public ApiResponse(int statusCode,String message,boolean success, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.success = success;
        this.data = data;
    }
}
