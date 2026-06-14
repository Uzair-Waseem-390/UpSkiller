package com.example.upskiller.model.request;

import com.google.gson.annotations.SerializedName;

public class SkillRequest {
    @SerializedName("skill_name") private final String skillName;
    @SerializedName("level")      private final String level;

    public SkillRequest(String skillName, String level) {
        this.skillName = skillName;
        this.level = level;
    }

    public String getSkillName() { return skillName; }
    public String getLevel()     { return level; }
}
