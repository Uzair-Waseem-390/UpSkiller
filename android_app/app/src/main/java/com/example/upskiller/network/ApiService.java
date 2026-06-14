package com.example.upskiller.network;

import com.example.upskiller.model.Roadmap;
import com.example.upskiller.model.Skill;
import com.example.upskiller.model.request.BulkSkillRequest;
import com.example.upskiller.model.request.LoginRequest;
import com.example.upskiller.model.request.RegisterRequest;
import com.example.upskiller.model.request.RoadmapCreateRequest;
import com.example.upskiller.model.request.SkillRequest;
import com.example.upskiller.model.request.TopicCompleteRequest;
import com.example.upskiller.model.response.AuthResponse;
import com.example.upskiller.model.response.RoadmapResponse;
import com.example.upskiller.model.response.SkillResponse;
import com.example.upskiller.model.response.SkillStatsResponse;
import com.example.upskiller.model.response.TopicCompleteResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // ── Auth ─────────────────────────────────────────────────────────────────
    @POST(ApiConstants.REGISTER)
    Call<AuthResponse> register(@Body RegisterRequest body);

    @POST(ApiConstants.LOGIN)
    Call<AuthResponse> login(@Body LoginRequest body);

    @POST(ApiConstants.LOGOUT)
    Call<Void> logout(@Body Map<String, String> body);

    @POST(ApiConstants.REFRESH_TOKEN)
    Call<AuthResponse> refreshToken(@Body Map<String, String> body);

    // ── Profile ───────────────────────────────────────────────────────────────
    @GET(ApiConstants.PROFILE)
    Call<AuthResponse> getProfile();

    @Multipart
    @PATCH(ApiConstants.PROFILE)
    Call<AuthResponse> updateProfile(
            @Part("name") RequestBody name,
            @Part MultipartBody.Part picture);

    @PATCH(ApiConstants.PROFILE)
    Call<AuthResponse> updateProfileName(@Body Map<String, String> body);

    @DELETE(ApiConstants.PROFILE_DELETE)
    Call<Void> deleteAccount();

    // ── Skills ────────────────────────────────────────────────────────────────
    @GET(ApiConstants.SKILLS)
    Call<List<Skill>> getSkills();

    @POST(ApiConstants.SKILLS)
    Call<SkillResponse> createSkill(@Body SkillRequest body);

    @POST(ApiConstants.SKILLS_BULK)
    Call<SkillResponse> bulkCreateSkills(@Body BulkSkillRequest body);

    @GET(ApiConstants.SKILLS_STATS)
    Call<SkillStatsResponse> getSkillStats();

    @PATCH("api/users/skills/{pk}/")
    Call<SkillResponse> updateSkill(@Path("pk") int pk, @Body SkillRequest body);

    @DELETE("api/users/skills/{pk}/")
    Call<Void> deleteSkill(@Path("pk") int pk);

    // ── Roadmaps ──────────────────────────────────────────────────────────────
    @POST(ApiConstants.ROADMAP_CREATE)
    Call<RoadmapResponse> createRoadmap(@Body RoadmapCreateRequest body);

    @GET(ApiConstants.ROADMAP_ACTIVE)
    Call<List<Roadmap>> getActiveRoadmaps();

    @GET(ApiConstants.ROADMAP_COMPLETED)
    Call<List<Roadmap>> getCompletedRoadmaps();

    @GET("api/roadmaps/{pk}/")
    Call<Roadmap> getRoadmapDetail(@Path("pk") int pk);

    @DELETE("api/roadmaps/{roadmap_id}/delete/")
    Call<Void> deleteRoadmap(@Path("roadmap_id") int id);

    // ── Topics ────────────────────────────────────────────────────────────────
    @POST("api/roadmaps/topic/{topic_id}/complete/")
    Call<TopicCompleteResponse> markTopicComplete(
            @Path("topic_id") int topicId,
            @Body TopicCompleteRequest body);
}
