package com.example.upskiller.model.request;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BulkSkillRequest {
    @SerializedName("skills") private final List<SkillRequest> skills;

    public BulkSkillRequest(List<SkillRequest> skills) {
        this.skills = skills;
    }
}
