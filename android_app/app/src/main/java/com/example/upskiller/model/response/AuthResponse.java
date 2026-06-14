package com.example.upskiller.model.response;

import com.example.upskiller.model.Tokens;
import com.example.upskiller.model.User;
import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("user")    private User user;
    @SerializedName("tokens")  private Tokens tokens;
    @SerializedName("message") private String message;

    public User getUser()      { return user; }
    public Tokens getTokens()  { return tokens; }
    public String getMessage() { return message; }
}
