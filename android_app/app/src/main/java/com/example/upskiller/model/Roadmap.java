package com.example.upskiller.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Roadmap {
    @SerializedName("id")                    private int id;
    @SerializedName("title")                 private String title;
    @SerializedName("target_skill")          private String targetSkill;
    @SerializedName("target_level")          private String targetLevel;
    @SerializedName("status")                private String status;
    @SerializedName("phases")               private List<Phase> phases;
    @SerializedName("progress_percentage")   private int progressPercentage;
    @SerializedName("is_completed")          private boolean isCompleted;
    @SerializedName("created_at")            private String createdAt;
    @SerializedName("completed_at")          private String completedAt;

    public int getId()                   { return id; }
    public String getTitle()             { return title; }
    public String getTargetSkill()       { return targetSkill; }
    public String getTargetLevel()       { return targetLevel; }
    public String getStatus()            { return status; }
    public List<Phase> getPhases()       { return phases; }
    public int getProgressPercentage()   { return progressPercentage; }
    public boolean isCompleted()         { return isCompleted; }
    public String getCreatedAt()         { return createdAt; }
    public String getCompletedAt()       { return completedAt; }

    public void setProgressPercentage(int p) { this.progressPercentage = p; }
    public void setPhases(List<Phase> phases) { this.phases = phases; }
}
