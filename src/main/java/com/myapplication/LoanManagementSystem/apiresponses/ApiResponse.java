package com.myapplication.LoanManagementSystem.apiresponses;


public class ApiResponse {
    public String getToken() {
        return token;
    }

    public ApiResponse(String token, String message, int statusCode) {
        this.token = token;
        this.message = message;
        this.statusCode = statusCode;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    private String token;
    private  String message;
    private int statusCode;


}
