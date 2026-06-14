package com.example.upskiller.model.response;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class SkillStatsResponse {
    @SerializedName("total_skills") private int totalSkills;
    @SerializedName("levels")       private Map<String, Integer> levels;

    public int getTotalSkills()             { return totalSkills; }
    public Map<String, Integer> getLevels() { return levels; }

    public int getBeginner()     { return levels != null && levels.containsKey("beginner")     ? levels.get("beginner")     : 0; }
    public int getIntermediate() { return levels != null && levels.containsKey("intermediate") ? levels.get("intermediate") : 0; }
    public int getAdvanced()     { return levels != null && levels.containsKey("advanced")     ? levels.get("advanced")     : 0; }
}
