package com.example.upskiller.model;

import com.google.gson.annotations.SerializedName;

public class Tokens {
    @SerializedName("access")  private String access;
    @SerializedName("refresh") private String refresh;

    public String getAccess()  { return access; }
    public String getRefresh() { return refresh; }
}
