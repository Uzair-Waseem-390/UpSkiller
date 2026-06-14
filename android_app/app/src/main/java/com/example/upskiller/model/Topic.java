package com.example.upskiller.model;

import com.google.gson.annotations.SerializedName;

public class Topic {
    @SerializedName("id")           private int id;
    @SerializedName("title")        private String title;
    @SerializedName("is_completed") private boolean isCompleted;
    @SerializedName("completed_at") private String completedAt;
    @SerializedName("order")        private int order;

    public int getId()              { return id; }
    public String getTitle()        { return title; }
    public boolean isCompleted()    { return isCompleted; }
    public String getCompletedAt()  { return completedAt; }
    public int getOrder()           { return order; }

    public void setCompleted(boolean completed) { this.isCompleted = completed; }
}
