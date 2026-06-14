# UpSkiller 🚀

> **AI-Powered Personalized Learning Roadmap Mobile Application**

UpSkiller generates personalized learning roadmaps based on your existing skills and desired goals. Unlike traditional static roadmaps, it skips what you already know and focuses only on the gaps — giving every learner a unique path.

---

## 👥 Team

| Name | AG Number |
|------|-----------|
| Uzair Waseem | 2023-AG-10020 |
| Saira Fatima Habib | 2023-AG-10010 |

**University of Agriculture, Faisalabad — BS Software Engineering**

---

## 📁 Repository Structure

```
upskiller/
├── backend/                  # Django REST API + LangGraph AI agent
├── android_app/              # Native Android application (Java)
└── UpSkiller_documentation.pdf  # Project SRS & documentation
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Django, Django REST Framework |
| AI Layer | LangGraph + Gemini 2.5 Flash |
| Frontend | Native Android (Java) |
| Database | SQLite (Dev) / PostgreSQL (Production) |

---

## ✨ What Makes UpSkiller Different

| Traditional Roadmaps | UpSkiller |
|----------------------|-----------|
| One roadmap for everyone | Different roadmap for every user |
| Teaches what you already know | Skips what you already know |
| Wastes your time | Saves your time |

---

## 🔄 How It Works

### 1. Register & Build Your Skill Profile

Sign up with name, email, and password. Right after registration, you're prompted to add your existing skills:

| Skill | Level |
|-------|-------|
| Python | Advanced |
| FastAPI | Intermediate |
| Docker | Beginner |

Skill levels: `Beginner` · `Intermediate` · `Advanced`

---

### 2. Generate a Roadmap

Enter the skill you want to learn and your target level:

```
Skill to Learn  →  MCP Servers
Target Level    →  Advanced
```

---

### 3. AI Personalization

The system sends your full skill profile + target to **Gemini 2.5 Flash** via a **LangGraph agent**. The AI:
- Analyzes what you already know
- Skips topics you've already mastered
- Generates only the missing knowledge

---

### 4. Structured AI Response

The AI returns **structured JSON only** — no chat, no fluff:

```json
{
  "roadmap_title": "MCP Servers",
  "target_level": "Advanced",
  "phases": [
    {
      "name": "Foundation",
      "topics": [
        { "title": "MCP Fundamentals" },
        { "title": "Tool Calling" }
      ]
    },
    {
      "name": "Intermediate",
      "topics": [
        { "title": "Transport Protocols" },
        { "title": "Authentication" }
      ]
    },
    {
      "name": "Advanced",
      "topics": [
        { "title": "Production Deployment" }
      ]
    }
  ]
}
```

---

### 5. Visual Roadmap on Android

The Android app renders this as a real roadmap — not chat bubbles:

```
FOUNDATION
  ☐  MCP Fundamentals
  ☐  Tool Calling

INTERMEDIATE
  ☐  Transport Protocols
  ☐  Authentication

ADVANCED
  ☐  Production Deployment
```

---

### 6. Track & Complete Topics

Mark topics one by one as you learn them:

```
FOUNDATION
  ✓  MCP Fundamentals
  ✓  Tool Calling

INTERMEDIATE
  ✓  Transport Protocols
  ☐  Authentication
```

---

### 7. Automatic Roadmap Completion

When **all topics** across all phases are marked done, the roadmap status automatically becomes `COMPLETED`. No manual button needed.

---

### 8. Automatic Skill Addition

After a roadmap completes, the skill is **automatically added** to your profile:

```
Completed:  MCP Servers → Advanced
Auto-added: MCP Servers → Advanced  ✓
```

This makes every future roadmap smarter — the AI has more context about you.

---

### 9. Multiple Roadmaps & History

Run multiple roadmaps at the same time and view your full history:

**In Progress:** Django · Docker · MCP Servers  
**Completed:** Git · Python · FastAPI

Nothing is ever deleted.

---

## 🗄️ Database Schema

```
User          → id, name, email, password, profile_picture
Skill         → id, user(FK), skill_name, level
Roadmap       → id, user(FK), title, target_level, status, created_at
Phase         → id, roadmap(FK), phase_name, order
Topic         → id, phase(FK), title, is_completed
```

---

## 🏗️ System Architecture

```
Android App (Java)
        ↓
Django REST API
        ↓
LangGraph Agent
        ↓
Gemini 2.5 Flash
        ↓
JSON Response
        ↓
Database Storage
```

---

## 📋 Functional Requirements

| ID | Feature | Description |
|----|---------|-------------|
| FR-1 | User Registration | Register with name, email, password, optional profile picture |
| FR-2 | User Login | Authenticate with email and password |
| FR-3 | Skill Setup | Prompt to add existing skills after registration |
| FR-4 | Skill Management | Add, update, delete, and view skills anytime |
| FR-5 | Roadmap Generation | Submit target skill and level to generate a roadmap |
| FR-6 | AI Personalization | AI skips known topics, generates only missing content |
| FR-7 | Structured Response | AI returns structured JSON, not conversational text |
| FR-8 | Roadmap Storage | All roadmaps persisted in the database |
| FR-9 | Multiple Roadmaps | Multiple active roadmaps simultaneously |
| FR-10 | Topic Completion | Mark individual topics as complete |
| FR-11 | Auto-Completion | Roadmap auto-completes when all topics are done |
| FR-12 | Skill Auto-Add | Target skill auto-added to profile on completion |
| FR-13 | Roadmap History | View active and completed roadmaps |
| FR-14 | Profile Management | Update name, profile picture, and skills |

---

## ⚙️ Non-Functional Requirements

- **Performance** — API responses within 2 seconds for normal operations
- **Reliability** — Data persists across app restarts
- **Usability** — Simple interface with clear roadmap visualization
- **Maintainability** — Modular architecture for easy future changes
- **Scalability** — Backend supports migration to PostgreSQL

---

## ⚠️ Constraints

- Internet connection required for AI roadmap generation
- Roadmap generation depends on Gemini API availability
- Personalization quality depends on accuracy of user-provided skills

---

## 🔮 Future Enhancements

- Resource recommendations and video links per topic
- Progress percentage tracking
- Search across roadmaps and topics
- Push notifications for learning reminders
- AI mentor chat for contextual help
- Skill analytics and learning streaks
- Dark mode

---

## 📄 Documentation

Full SRS is available in [`UpSkiller_documentation.pdf`](./UpSkiller_documentation.pdf).