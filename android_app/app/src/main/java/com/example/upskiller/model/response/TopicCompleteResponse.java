package com.example.upskiller.model.response;

import com.example.upskiller.model.Roadmap;
import com.example.upskiller.model.Topic;
import com.google.gson.annotations.SerializedName;

public class TopicCompleteResponse {
    @SerializedName("topic")   private Topic topic;
    @SerializedName("roadmap") private Roadmap roadmap;
    @SerializedName("message") private String message;

    public Topic getTopic()     { return topic; }
    public Roadmap getRoadmap() { return roadmap; }
    public String getMessage()  { return message; }
}
