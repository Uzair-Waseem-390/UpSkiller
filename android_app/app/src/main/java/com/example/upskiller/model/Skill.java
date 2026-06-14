package com.example.upskiller.model;

import com.google.gson.annotations.SerializedName;

public class Skill {
    @SerializedName("id")           private int id;
    @SerializedName("skill_name")   private String skillName;
    @SerializedName("level")        private String level;
    @SerializedName("level_display") private String levelDisplay;
    @SerializedName("created_at")   private String createdAt;

    public int getId()              { return id; }
    public String getSkillName()    { return skillName; }
    public String getLevel()        { return level; }
    public String getLevelDisplay() { return levelDisplay; }
    public String getCreatedAt()    { return createdAt; }

    public void setSkillName(String skillName) { this.skillName = skillName; }
    public void setLevel(String level)         { this.level = level; }
}
