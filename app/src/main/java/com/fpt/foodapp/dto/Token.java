package com.fpt.foodapp.dto;

public class Token {
    private String token;
    private boolean isToken;


    public Token() {
    }

    public Token(String token, boolean isToken) {
        this.token = token;
        this.isToken = isToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isToken() {
        return isToken;
    }

    public void setToken(boolean token) {
        isToken = token;
    }
}
