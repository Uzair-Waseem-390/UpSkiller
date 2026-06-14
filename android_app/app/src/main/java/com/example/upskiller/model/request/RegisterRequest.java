package com.example.upskiller.model.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("email")            private final String email;
    @SerializedName("name")             private final String name;
    @SerializedName("password")         private final String password;
    @SerializedName("password_confirm") private final String passwordConfirm;

    public RegisterRequest(String email, String name, String password, String passwordConfirm) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
