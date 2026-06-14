# UpSkiller Android App — Technical Design

## Tech Stack
- Language: Core Java (no Kotlin)
- HTTP: Retrofit2 + OkHttp3 + Gson
- UI: Material Design 3, ConstraintLayout, RecyclerView
- Auth: JWT stored in SharedPreferences via SessionManager
- Image loading: Glide
- Min SDK 24 / Target SDK 36

---

## Package Structure

```
com.example.upskiller
├── data
│   ├── api
│   │   ├── ApiClient.java          # Retrofit singleton
│   │   ├── ApiConstants.java       # All endpoint strings + base URL
│   │   ├── AuthInterceptor.java    # Attaches Bearer token
│   │   ├── TokenAuthenticator.java # Silent refresh on 401
│   │   ├── UserApi.java            # Retrofit interface — users
│   │   └── RoadmapApi.java         # Retrofit interface — roadmaps
│   ├── model
│   │   ├── User.java
│   │   ├── Skill.java
│   │   ├── Roadmap.java
│   │   ├── Phase.java
│   │   ├── Topic.java
│   │   └── SkillStats.java
│   └── request / response POJOs
│       ├── LoginRequest.java
│       ├── RegisterRequest.java
│       ├── AuthResponse.java
│       ├── SkillRequest.java
│       ├── BulkSkillRequest.java
│       ├── RoadmapCreateRequest.java
│       └── TopicCompleteRequest.java
├── session
│   └── SessionManager.java         # SharedPreferences wrapper
├── ui
│   ├── base
│   │   ├── BaseActivity.java       # Toolbar + progress dialog helpers
│   │   └── BaseFragment.java       # Snackbar + loading helpers
│   ├── auth
│   │   ├── LoginActivity.java
│   │   └── RegisterActivity.java
│   ├── onboarding
│   │   └── SkillOnboardingActivity.java
│   ├── main
│   │   └── MainActivity.java       # Bottom nav host
│   ├── home
│   │   ├── HomeFragment.java       # ViewPager2 — Active / Completed tabs
│   │   ├── RoadmapListFragment.java
│   │   └── RoadmapListAdapter.java
│   ├── roadmap
│   │   ├── CreateRoadmapFragment.java
│   │   ├── RoadmapDetailActivity.java
│   │   ├── PhaseAdapter.java
│   │   └── TopicAdapter.java
│   └── profile
│       ├── ProfileFragment.java
│       └── EditProfileActivity.java
└── util
    ├── ApiCallback.java            # Generic Retrofit callback wrapper
    ├── NetworkUtils.java           # Connectivity check
    └── UiUtils.java                # Snackbar / Toast helpers
```

---

## Core Components

### ApiClient.java
Single OkHttpClient with `AuthInterceptor` and `TokenAuthenticator` attached.
One Retrofit instance built from it. Static `getInstance()` returns it.

```
OkHttpClient
  └── AuthInterceptor      → adds "Authorization: Bearer <access>"
  └── TokenAuthenticator   → on 401: calls /auth/refresh/, retries request
Retrofit
  └── GsonConverterFactory
  └── base url from ApiConstants.BASE_URL
```

### SessionManager.java
Wraps SharedPreferences. Methods:
- `saveTokens(String access, String refresh)`
- `getAccessToken()` / `getRefreshToken()`
- `saveUser(User user)` / `getUser()`
- `isLoggedIn()` — returns true if access token is non-null
- `clearSession()` — wipes all stored data

### ApiCallback<T>
Abstract Retrofit `Callback<T>` subclass. Subclasses only implement `onSuccess(T body)` and `onFailure(String message)`. Handles HTTP error body parsing and network exceptions internally.

---

## Screen Flow

```
App Launch
  └── SplashActivity (check SessionManager.isLoggedIn())
        ├── logged in  → MainActivity (bottom nav)
        └── not logged → LoginActivity
                          └── RegisterActivity
                                └── SkillOnboardingActivity → MainActivity
```

### MainActivity — Bottom Navigation
Three tabs via `BottomNavigationView` + `NavHostFragment`:
1. Home (HomeFragment)
2. New Roadmap (CreateRoadmapFragment)
3. Profile (ProfileFragment)

### HomeFragment
`ViewPager2` + `TabLayout` with two tabs:
- Tab 0: `RoadmapListFragment(status=active)`
- Tab 1: `RoadmapListFragment(status=completed)`

`RoadmapListFragment` is reused for both tabs, parameterised by a String argument `"active"` or `"completed"`.

### RoadmapDetailActivity
Launched from `RoadmapListAdapter` with `roadmapId` extra.
Fetches `GET /api/roadmaps/{pk}/` and renders:
- Header: title, skill chip, level chip, circular ProgressBar, percentage text
- `RecyclerView` of phases using `PhaseAdapter`
- Each phase row is expandable; inside it embeds a `TopicAdapter`

Topic checkbox `onCheckedChanged` → calls `POST /api/roadmaps/topic/{id}/complete/` → on response updates `progressPercentage` in header.

---

## API Interfaces

### UserApi.java
```java
@POST("auth/register/")  Call<AuthResponse> register(@Body RegisterRequest body);
@POST("auth/login/")     Call<AuthResponse> login(@Body LoginRequest body);
@POST("auth/logout/")    Call<Void> logout(@Body Map<String,String> body);
@POST("auth/refresh/")   Call<AuthResponse> refreshToken(@Body Map<String,String> body);
@GET("profile/")         Call<UserResponse> getProfile();
@PATCH("profile/")       Call<UserResponse> updateProfile(@Body RequestBody body);  // multipart for picture
@DELETE("profile/delete/") Call<Void> deleteAccount();
@GET("skills/")          Call<List<Skill>> getSkills();
@POST("skills/")         Call<SkillResponse> createSkill(@Body SkillRequest body);
@POST("skills/bulk/")    Call<BulkSkillResponse> bulkCreateSkills(@Body BulkSkillRequest body);
@GET("skills/stats/")    Call<SkillStats> getSkillStats();
@PATCH("skills/{pk}/")   Call<SkillResponse> updateSkill(@Path("pk") int pk, @Body SkillRequest body);
@DELETE("skills/{pk}/")  Call<Void> deleteSkill(@Path("pk") int pk);
```

### RoadmapApi.java
```java
@POST("create/")                    Call<RoadmapResponse> createRoadmap(@Body RoadmapCreateRequest body);
@GET("active/")                     Call<List<Roadmap>> getActiveRoadmaps();
@GET("completed/")                  Call<List<Roadmap>> getCompletedRoadmaps();
@GET("{pk}/")                       Call<Roadmap> getRoadmapDetail(@Path("pk") int pk);
@DELETE("{roadmap_id}/delete/")     Call<Void> deleteRoadmap(@Path("roadmap_id") int id);
@POST("topic/{topic_id}/complete/") Call<TopicCompleteResponse> markTopicComplete(@Path("topic_id") int id, @Body TopicCompleteRequest body);
```

---

## Key Data Models

```java
// User.java
int id; String email, name, profilePicture; String createdAt;

// Skill.java  (field names match JSON snake_case via @SerializedName)
int id; String skillName, level, levelDisplay;

// Roadmap.java
int id; String title, targetSkill, targetLevel, status;
int progressPercentage; boolean isCompleted;
List<Phase> phases; String createdAt, completedAt;

// Phase.java
int id, order; String name;
int completionPercentage; boolean isCompleted;
List<Topic> topics;

// Topic.java
int id, order; String title; boolean isCompleted; String completedAt;

// SkillStats.java
int totalSkills; Map<String,Integer> levels;  // beginner/intermediate/advanced counts

// AuthResponse.java
User user; Tokens tokens; String message;

// Tokens.java
String access, refresh;
```

---

## Layouts (screens)

| File | Description |
|---|---|
| `activity_login.xml` | Email + password + login button + register link |
| `activity_register.xml` | Name + email + password + confirm + register button |
| `activity_skill_onboarding.xml` | RecyclerView of added skills + FAB to add + Next button |
| `activity_main.xml` | FragmentContainerView + BottomNavigationView |
| `fragment_home.xml` | TabLayout + ViewPager2 |
| `fragment_roadmap_list.xml` | RecyclerView + empty state |
| `item_roadmap.xml` | Card: title, skill chip, level chip, LinearProgressIndicator + % |
| `fragment_create_roadmap.xml` | TextInputLayout (skill) + chip group (level) + Generate button |
| `activity_roadmap_detail.xml` | CollapsingToolbarLayout header + RecyclerView (phases) |
| `item_phase.xml` | Phase name + completion % + expand/collapse arrow |
| `item_topic.xml` | CheckBox + topic title |
| `fragment_profile.xml` | CircleImageView + name + email + skill stats card + edit/logout/delete |
| `activity_edit_profile.xml` | Name field + profile picture picker |
| `dialog_add_skill.xml` | TextInputLayout + chip group (level) |

---

## Gradle Dependencies to Add

```kotlin
// Retrofit + Gson
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Glide (profile picture + avatar)
implementation("com.github.bumptech.glide:glide:4.16.0")
annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

// CircleImageView
implementation("de.hdodenhof:circleimageview:3.1.0")

// ViewPager2 (tabs in Home)
implementation("androidx.viewpager2:viewpager2:1.1.0")
