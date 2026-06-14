# UpSkiller Android App — Requirements

## Overview
A native Android application (Core Java) that consumes the UpSkiller Django REST backend.
The app allows users to register, manage their skills, generate AI-driven learning roadmaps,
and track their progress topic-by-topic.

---

## R1 — Authentication

### R1.1 Registration
- The app MUST provide a registration screen with fields: Full Name, Email, Password, Confirm Password.
- On successful registration the backend returns `{ user, tokens }`. The app MUST persist both `access` and `refresh` tokens in SharedPreferences and navigate the user to the Skills setup screen.
- Validation errors returned by the backend MUST be shown inline on the relevant field.

### R1.2 Login
- The app MUST provide a login screen with fields: Email, Password.
- On success, tokens and user data MUST be persisted and the user navigated to the Home screen.
- On failure (`401`), a clear error message MUST be shown.

### R1.3 Token Refresh
- When any API call returns `401`, the app MUST automatically attempt a silent token refresh using the stored `refresh_token` via `POST /api/users/auth/refresh/`.
- If the refresh succeeds, the original request MUST be retried with the new `access` token.
- If the refresh fails, the app MUST clear stored tokens and redirect to the Login screen.

### R1.4 Logout
- Authenticated users MUST be able to log out from the Profile screen.
- Logout MUST call `POST /api/users/auth/logout/` with the stored `refresh_token`, then clear all persisted tokens and navigate to Login.

---

## R2 — Skill Management

### R2.1 Skill List
- After login/register, the app MUST display the user's skills fetched from `GET /api/users/skills/`.
- Each skill item shows: skill name and level badge (Beginner / Intermediate / Advanced).

### R2.2 Add Skill
- The user MUST be able to add a skill via a dialog or bottom sheet with: skill name (text input) and level (dropdown/chips: beginner, intermediate, advanced).
- Calls `POST /api/users/skills/`.

### R2.3 Edit Skill
- The user MUST be able to edit an existing skill's name or level.
- Calls `PATCH /api/users/skills/{pk}/`.

### R2.4 Delete Skill
- The user MUST be able to delete a skill with a confirmation prompt.
- Calls `DELETE /api/users/skills/{pk}/`.

### R2.5 Bulk Skill Add (Onboarding)
- On first registration, the user MUST be guided through an onboarding screen where they can add multiple skills at once before entering the main app.
- Calls `POST /api/users/skills/bulk/`.

### R2.6 Skill Stats
- The Profile screen MUST show a skill stats summary: total skills, count per level, fetched from `GET /api/users/skills/stats/`.

---

## R3 — Roadmap Management

### R3.1 Create Roadmap
- The user MUST be able to generate a new AI-powered roadmap by providing:
  - Target Skill (text input, min 2 chars)
  - Target Level (dropdown/chips: beginner, intermediate, advanced)
- Calls `POST /api/roadmaps/create/`. The generation may take time; a loading indicator MUST be shown.
- On success, the app navigates directly to the new roadmap's detail screen.

### R3.2 Roadmap List (Home)
- The Home screen MUST show two tabs: **Active** and **Completed** roadmaps.
- Active tab calls `GET /api/roadmaps/active/`.
- Completed tab calls `GET /api/roadmaps/completed/`.
- Each list item shows: title, target skill, target level, and a progress bar with percentage.

### R3.3 Roadmap Detail
- Tapping a roadmap opens its detail screen fetched from `GET /api/roadmaps/{pk}/`.
- The detail screen shows:
  - Roadmap title, skill, level, overall progress percentage (circular or linear progress).
  - A list of **Phases**, each expandable/collapsible.
  - Inside each Phase: a list of **Topics** with a checkbox to mark complete/incomplete.

### R3.4 Topic Toggle
- Tapping a topic checkbox calls `POST /api/roadmaps/topic/{topic_id}/complete/` with `{ is_completed: bool }`.
- The UI MUST update optimistically and reflect the server response (updated progress percentage).

### R3.5 Delete Roadmap
- From the roadmap detail screen (or list via swipe/long-press), the user MUST be able to delete a roadmap after a confirmation dialog.
- Calls `DELETE /api/roadmaps/{roadmap_id}/delete/`.

---

## R4 — Profile

### R4.1 View Profile
- A Profile screen MUST show the user's name, email, profile picture (or default avatar if none), and skill stats.

### R4.2 Edit Profile
- The user MUST be able to update their name and upload a new profile picture.
- Calls `PATCH /api/users/profile/`.

### R4.3 Delete Account
- The user MUST be able to delete their account with a strong confirmation dialog.
- Calls `DELETE /api/users/profile/delete/`, then clears tokens and navigates to Login.

---

## R5 — Navigation

### R5.1 Bottom Navigation
- Authenticated screens MUST use a bottom navigation bar with three destinations:
  - **Home** — Roadmap list
  - **New Roadmap** — Create roadmap screen
  - **Profile** — Profile screen

### R5.2 Back Stack
- The app MUST handle the Android back stack correctly: pressing back from Home exits the app; pressing back from any detail screen returns to the list.

---

## R6 — Architecture & Technical

### R6.1 Language
- All Android source code MUST be written in Core Java (no Kotlin).

### R6.2 HTTP Client
- All network calls MUST use **Retrofit2** with **Gson** converter.
- A single `ApiClient` singleton MUST be used across the app.
- An OkHttp `AuthInterceptor` MUST attach `Authorization: Bearer {access_token}` to all authenticated requests.

### R6.3 Token Storage
- JWT tokens (`access`, `refresh`) and user data MUST be stored in `SharedPreferences` via a single `SessionManager` helper class.

### R6.4 Error Handling
- All API errors MUST be caught and shown to the user via a `Snackbar` or `Toast`.
- Network errors (no internet) MUST show a user-friendly message.

### R6.5 DRY / Reusability
- A `BaseActivity` and `BaseFragment` MUST encapsulate shared setup (toolbar, progress dialog).
- A reusable `ApiCallback<T>` wrapper MUST handle success/error consistently across all calls.
- All API endpoint constants MUST live in a single `ApiConstants` class.
- String resources MUST be used for all user-visible text (no hardcoded strings in Java).

### R6.6 Minimum SDK
- minSdk = 24 (Android 7.0), targetSdk = 36 as per existing build config.
