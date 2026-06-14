package com.example.upskiller.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")        private int id;
    @SerializedName("email")     private String email;
    @SerializedName("name")      private String name;
    @SerializedName("profile_picture") private String profilePicture;
    @SerializedName("created_at") private String createdAt;

    public int getId()              { return id; }
    public String getEmail()        { return email; }
    public String getName()         { return name; }
    public String getProfilePicture() { return profilePicture; }
    public String getCreatedAt()    { return createdAt; }
}
