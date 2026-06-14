package com.example.upskiller.model.response;

import com.example.upskiller.model.Roadmap;
import com.google.gson.annotations.SerializedName;

public class RoadmapResponse {
    @SerializedName("roadmap") private Roadmap roadmap;
    @SerializedName("message") private String message;

    public Roadmap getRoadmap() { return roadmap; }
    public String getMessage()  { return message; }
}
