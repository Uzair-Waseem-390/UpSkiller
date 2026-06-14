package com.example.upskiller.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Phase {
    @SerializedName("id")                    private int id;
    @SerializedName("name")                  private String name;
    @SerializedName("order")                 private int order;
    @SerializedName("topics")               private List<Topic> topics;
    @SerializedName("completion_percentage") private int completionPercentage;
    @SerializedName("is_completed")          private boolean isCompleted;

    public int getId()                    { return id; }
    public String getName()               { return name; }
    public int getOrder()                 { return order; }
    public List<Topic> getTopics()        { return topics; }
    public int getCompletionPercentage()  { return completionPercentage; }
    public boolean isCompleted()          { return isCompleted; }

    public void setCompletionPercentage(int p) { this.completionPercentage = p; }
}
