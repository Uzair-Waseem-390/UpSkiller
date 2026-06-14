# UpSkiller Android App — Tasks

## Task 1: Project Setup & Dependencies
Configure build.gradle.kts, libs.versions.toml, and AndroidManifest.xml. Add Retrofit2, OkHttp3, Gson, Glide, CircleImageView, ViewPager2 dependencies. Convert MainActivity from Kotlin to Java. Add INTERNET permission and network security config for local dev server.

- [ ] 1.1 Update `libs.versions.toml` with retrofit, okhttp, gson, glide, circleimageview, viewpager2 versions
- [ ] 1.2 Update `app/build.gradle.kts` to apply the new dependencies
- [ ] 1.3 Add `<uses-permission android:name="android.permission.INTERNET"/>` to AndroidManifest.xml
- [ ] 1.4 Add `android:usesCleartextTraffic="true"` (or network_security_config.xml) for local HTTP dev server
- [ ] 1.5 Delete `MainActivity.kt`, create `MainActivity.java` skeleton
- [ ] 1.6 Sync and verify the project builds without errors

## Task 2: Data Layer — Models & API Interfaces
Create all POJO model classes and Retrofit API interfaces matching the backend response shapes.

- [ ] 2.1 Create `data/model/User.java`, `Skill.java`, `Roadmap.java`, `Phase.java`, `Topic.java`, `SkillStats.java`
- [ ] 2.2 Create request POJOs: `LoginRequest.java`, `RegisterRequest.java`, `SkillRequest.java`, `BulkSkillRequest.java`, `RoadmapCreateRequest.java`, `TopicCompleteRequest.java`
- [ ] 2.3 Create response POJOs: `AuthResponse.java`, `Tokens.java`, `SkillResponse.java`, `BulkSkillResponse.java`, `RoadmapResponse.java`, `TopicCompleteResponse.java`, `UserResponse.java`
- [ ] 2.4 Create `data/api/ApiConstants.java` with BASE_URL and all endpoint path constants
- [ ] 2.5 Create `data/api/UserApi.java` Retrofit interface with all user/auth/skill endpoints
- [ ] 2.6 Create `data/api/RoadmapApi.java` Retrofit interface with all roadmap/topic endpoints

## Task 3: Networking Core — ApiClient, SessionManager, Interceptors
Build the single Retrofit/OkHttp instance with auth injection and automatic token refresh.

- [ ] 3.1 Create `session/SessionManager.java` — SharedPreferences wrapper for tokens + user
- [ ] 3.2 Create `data/api/AuthInterceptor.java` — attaches `Authorization: Bearer <access>` header
- [ ] 3.3 Create `data/api/TokenAuthenticator.java` — on 401: refreshes token silently, retries; on failure clears session
- [ ] 3.4 Create `data/api/ApiClient.java` — singleton OkHttpClient + Retrofit with interceptor and authenticator
- [ ] 3.5 Create `util/ApiCallback.java` — generic abstract Retrofit Callback<T> with `onSuccess(T)` and `onFailure(String)`
- [ ] 3.6 Create `util/NetworkUtils.java` — connectivity check helper
- [ ] 3.7 Create `util/UiUtils.java` — static Snackbar/Toast helpers

## Task 4: Base UI Classes & Theme
Create BaseActivity, BaseFragment, and apply Material Design 3 theme across the app.

- [ ] 4.1 Create `ui/base/BaseActivity.java` — progress dialog show/hide, Snackbar helper, toolbar setup
- [ ] 4.2 Create `ui/base/BaseFragment.java` — loading state, Snackbar helper
- [ ] 4.3 Update `res/values/themes.xml` to use `Theme.Material3.DayNight.NoActionBar`
- [ ] 4.4 Update `res/values/colors.xml` with app color palette
- [ ] 4.5 Create `res/values/strings.xml` with all user-visible string resources

## Task 5: Authentication Screens
Build Login and Register screens with full validation and token persistence.

- [ ] 5.1 Create `activity_login.xml` — email + password fields + login button + register link
- [ ] 5.2 Create `LoginActivity.java` — validates input, calls `POST /api/users/auth/login/`, saves session, navigates
- [ ] 5.3 Create `activity_register.xml` — name + email + password + confirm password + register button
- [ ] 5.4 Create `RegisterActivity.java` — validates input + password match, calls `POST /api/users/auth/register/`, saves session, navigates to onboarding
- [ ] 5.5 Create `SplashActivity.java` + `activity_splash.xml` — checks `SessionManager.isLoggedIn()`, routes to Login or MainActivity; register it as the launcher in AndroidManifest.xml

## Task 6: Skill Onboarding Screen
First-time skill entry flow shown after registration.

- [ ] 6.1 Create `activity_skill_onboarding.xml` — RecyclerView of added skills + FAB to open add-skill dialog + "Get Started" button
- [ ] 6.2 Create `dialog_add_skill.xml` — TextInputLayout (skill name) + ChipGroup for level selection
- [ ] 6.3 Create `SkillOnboardingActivity.java` — manages in-memory skill list, renders added skills, on "Get Started" calls `POST /api/users/skills/bulk/`, then navigates to MainActivity
- [ ] 6.4 Create `OnboardingSkillAdapter.java` — RecyclerView adapter for the onboarding skill list with delete capability

## Task 7: Main Activity & Bottom Navigation
Host activity with bottom nav routing to Home, Create Roadmap, and Profile fragments.

- [ ] 7.1 Create `activity_main.xml` — FragmentContainerView + BottomNavigationView (3 items: home, add, profile)
- [ ] 7.2 Create `res/menu/bottom_nav_menu.xml` with 3 items and icons
- [ ] 7.3 Implement `MainActivity.java` — sets up NavHostFragment with bottom nav, handles back stack
- [ ] 7.4 Create `res/navigation/nav_graph.xml` with fragments: HomeFragment, CreateRoadmapFragment, ProfileFragment

## Task 8: Home Screen — Roadmap List
Tabbed list of active and completed roadmaps with progress indicators.

- [ ] 8.1 Create `fragment_home.xml` — TabLayout + ViewPager2
- [ ] 8.2 Create `HomeFragment.java` — sets up ViewPager2 with RoadmapPagerAdapter (Active / Completed tabs)
- [ ] 8.3 Create `fragment_roadmap_list.xml` — RecyclerView + empty state TextView
- [ ] 8.4 Create `RoadmapListFragment.java` — accepts tab type argument, fetches active or completed roadmaps, handles empty state
- [ ] 8.5 Create `item_roadmap.xml` — MaterialCardView with title, target_skill chip, target_level chip, LinearProgressIndicator, progress % text
- [ ] 8.6 Create `RoadmapListAdapter.java` — binds Roadmap list, click opens RoadmapDetailActivity, long-press offers delete

## Task 9: Create Roadmap Screen
Form to generate a new AI-powered roadmap.

- [ ] 9.1 Create `fragment_create_roadmap.xml` — TextInputLayout (target skill) + ChipGroup (beginner/intermediate/advanced) + "Generate Roadmap" button
- [ ] 9.2 Create `CreateRoadmapFragment.java` — validates input, calls `POST /api/roadmaps/create/`, shows loading, on success launches RoadmapDetailActivity with new roadmap id

## Task 10: Roadmap Detail Screen
Full roadmap view with expandable phases and topic checkboxes.

- [ ] 10.1 Create `activity_roadmap_detail.xml` — AppBarLayout with CollapsingToolbarLayout (title/skill/level/progress) + NestedScrollView + RecyclerView (phases)
- [ ] 10.2 Create `item_phase.xml` — phase name, completion %, expand/collapse toggle
- [ ] 10.3 Create `item_topic.xml` — CheckBox + topic title, strikethrough text when completed
- [ ] 10.4 Create `TopicAdapter.java` — binds topics, checkbox toggle fires API call
- [ ] 10.5 Create `PhaseAdapter.java` — binds phases, manages expand/collapse of embedded TopicAdapter, shows completion % badge
- [ ] 10.6 Create `RoadmapDetailActivity.java` — fetches roadmap detail, wires PhaseAdapter, handles topic toggle API response (update progress header), delete roadmap via menu item

## Task 11: Profile Screen
View and edit profile, show skill stats, logout and delete account.

- [ ] 11.1 Create `fragment_profile.xml` — CircleImageView (avatar), name, email, SkillStats card (total + per-level counts), Edit button, Logout button, Delete Account button
- [ ] 11.2 Create `ProfileFragment.java` — fetches `GET /api/users/profile/` and `GET /api/users/skills/stats/`, binds data, wires buttons
- [ ] 11.3 Create `activity_edit_profile.xml` — name TextInputLayout + profile picture ImageView + change picture button
- [ ] 11.4 Create `EditProfileActivity.java` — handles name edit + image picker, calls `PATCH /api/users/profile/` (multipart if picture selected)
- [ ] 11.5 Wire Logout in ProfileFragment — calls `POST /api/users/auth/logout/`, clears session, navigates to LoginActivity
- [ ] 11.6 Wire Delete Account — confirmation dialog, calls `DELETE /api/users/profile/delete/`, clears session, navigates to LoginActivity

## Task 12: Skills Management (in-app)
Full CRUD for skills accessible from the Profile screen.

- [ ] 12.1 Add a "Manage Skills" entry point in ProfileFragment (button or card) that opens SkillsActivity
- [ ] 12.2 Create `activity_skills.xml` — RecyclerView + FAB to add skill
- [ ] 12.3 Create `item_skill.xml` — skill name + level chip + edit icon + delete icon
- [ ] 12.4 Create `SkillsAdapter.java` — binds skills, edit/delete callbacks
- [ ] 12.5 Create `SkillsActivity.java` — fetches skills, handles add/edit (reuse dialog_add_skill.xml), delete with confirmation
