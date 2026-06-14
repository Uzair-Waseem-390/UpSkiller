package com.example.upskiller.model.request;

import com.google.gson.annotations.SerializedName;

public class RoadmapCreateRequest {
    @SerializedName("target_skill")  private final String targetSkill;
    @SerializedName("target_level")  private final String targetLevel;

    public RoadmapCreateRequest(String targetSkill, String targetLevel) {
        this.targetSkill = targetSkill;
        this.targetLevel = targetLevel;
    }
}
