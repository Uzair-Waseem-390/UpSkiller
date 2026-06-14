package com.example.upskiller.model.response;

import com.example.upskiller.model.Skill;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SkillResponse {
    @SerializedName("skill")    private Skill skill;
    @SerializedName("created")  private List<Skill> created;
    @SerializedName("message")  private String message;
    @SerializedName("errors")   private List<String> errors;

    public Skill getSkill()          { return skill; }
    public List<Skill> getCreated()  { return created; }
    public String getMessage()       { return message; }
    public List<String> getErrors()  { return errors; }
}
