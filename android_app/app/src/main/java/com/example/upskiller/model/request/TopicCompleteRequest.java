package com.example.upskiller.model.request;

import com.google.gson.annotations.SerializedName;

public class TopicCompleteRequest {
    @SerializedName("is_completed") private final boolean isCompleted;

    public TopicCompleteRequest(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
