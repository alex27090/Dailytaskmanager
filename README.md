# 📋 Daily Task Manager — Android Foundation

A clean, well-structured Android task manager app built with modern Android development
best practices. Soft pastel UI, MVVM architecture, Room persistence, and reminder support.

---

## 🏗️ Architecture Overview

```
app/
└── src/main/java/com/dailytask/manager/
    ├── data/
    │   ├── local/
    │   │   ├── dao/          ← Room DAOs (TaskDao, CategoryDao)
    │   │   ├── entity/       ← Room Entities (TaskEntity, CategoryEntity)
    │   │   ├── TaskDatabase  ← Room DB + default category seeding
    │   │   └── Mappers.kt    ← Entity ↔ Domain model converters
    │   └── repository/       ← Repository implementations
    │       ├── TaskRepositoryImpl
    │       └── CategoryRepositoryImpl
    │
    ├── domain/
    │   ├── model/            ← Pure Kotlin domain models (Task, Category, Priority)
    │   └── usecase/          ← Repository interfaces
    │
    ├── presentation/
    │   ├── adapter/          ← RecyclerView adapters
    │   │   ├── TaskAdapter
    │   │   └── CategoryAdapter
    │   ├── ui/
    │   │   ├── MainActivity
    │   │   ├── home/         ← HomeFragment (daily view + date nav)
    │   │   ├── task/         ← AddEditTaskFragment
    │   │   └── category/     ← CategoryListFragment, AddCategoryFragment
    │   └── viewmodel/
    │       ├── HomeViewModel
    │       ├── TaskViewModel
    │       └── CategoryViewModel
    │
    ├── di/                   ← Hilt DI modules
    │   └── AppModule.kt
    │
    └── util/
        ├── ReminderScheduler ← AlarmManager-based reminder scheduling
        ├── ReminderReceiver  ← BroadcastReceiver → posts notification
        └── BootReceiver      ← Re-schedules reminders after device reboot
```

---

## ✅ What's Built (Foundation Features)

| Feature | Status | Details |
|---|---|---|
| Task CRUD | ✅ | Create, Read, Update, Delete with Room |
| Priority Levels | ✅ | Low / Medium / High with color-coded stripe |
| Categories | ✅ | Colored categories, 5 seeded on first launch |
| Due Dates | ✅ | DatePicker, overdue detection |
| Reminders | ✅ | AlarmManager exact alarm + notification |
| Daily Schedule View | ✅ | Date navigator (prev/next) with completion summary |
| Search | ✅ | SQL LIKE search by title + description (DAO ready) |
| Swipe to Delete | ✅ | With Undo via Snackbar |
| Boot persistence | ✅ | Reminders re-scheduled after reboot |
| Hilt DI | ✅ | Full dependency injection |
| StateFlow / Coroutines | ✅ | Reactive UI throughout |
| Unit Tests | ✅ | Repository + ViewModel tests |
| Soft Pastel UI | ✅ | Nunito font, pink/lavender/mint palette |

---

## 🚀 Setup Instructions

### 1. Clone / Import
Open the `DailyTaskManager` folder in Android Studio Hedgehog (2023.1.1) or newer.

### 2. Add Nunito Font
Download from [fonts.google.com/specimen/Nunito](https://fonts.google.com/specimen/Nunito)
and place the TTF files:
```
app/src/main/res/font/
├── nunito_regular.ttf
├── nunito_semibold.ttf
├── nunito_bold.ttf
└── nunito_extrabold.ttf
```

### 3. Sync Gradle
Click **Sync Now** — all dependencies download automatically.

### 4. Run
Connect a device or emulator (API 26+) and hit **▶ Run**.

---

## 📦 Tech Stack & Dependencies

| Library | Version | Purpose |
|---|---|---|
| Kotlin | 1.9.22 | Language |
| Room | 2.6.1 | Local SQLite database |
| Hilt | 2.50 | Dependency injection |
| Navigation | 2.7.6 | Fragment navigation + SafeArgs |
| Lifecycle / ViewModel | 2.7.0 | MVVM state management |
| Coroutines + Flow | 1.7.3 | Async + reactive streams |
| WorkManager | 2.9.0 | Background task support (ready to use) |
| Material3 | 1.11.0 | UI components |
| ThreeTenABP | 1.4.7 | java.time backport |

---

## 🗄️ Database Schema

### `tasks`
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | Auto-generated |
| title | TEXT | Required |
| description | TEXT | Optional |
| is_completed | INTEGER | Boolean (0/1) |
| priority | INTEGER | 0=Low, 1=Medium, 2=High |
| category_id | INTEGER FK | → categories.id, nullable |
| due_date | INTEGER | Epoch millis (date only) |
| reminder_time | INTEGER | Epoch millis (date + time) |
| created_at | INTEGER | Epoch millis |
| updated_at | INTEGER | Epoch millis |

### `categories`
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | Auto-generated |
| name | TEXT | Display name |
| color_hex | TEXT | e.g. `#FFB3C6` |
| icon_name | TEXT | Drawable resource name |
| created_at | INTEGER | Epoch millis |

---

## 🔮 Recommended Next Features

These are easy to add on top of this foundation:

1. **Search Screen** — Wire `TaskDao.searchTasks()` to a SearchView fragment
2. **Weekly/Monthly Calendar View** — Custom view or CalendarView integration
3. **Recurring Tasks** — Add a `recurrence_rule` column to TaskEntity
4. **Statistics Screen** — Query completion rates, streaks, category breakdowns
5. **Dark Theme** — `res/values-night/themes.xml` already structured for it
6. **Task Sorting/Filtering** — Add filter chips above the RecyclerView
7. **Widget** — AppWidgetProvider showing today's pending tasks
8. **Backup/Restore** — Room database export via `ContentResolver`
9. **Google Tasks Sync** — via Google Tasks REST API
10. **Subtasks** — A `subtasks` table with `parent_task_id` FK

---

## 🧪 Running Tests

```bash
# Unit tests (no device needed)
./gradlew test

# Instrumented tests (device/emulator required)
./gradlew connectedAndroidTest
```

---

## 📁 Key Files Quick Reference

| File | What it does |
|---|---|
| `TaskDatabase.kt` | Room DB, seeds 5 default categories on first launch |
| `Mappers.kt` | Converts Entity ↔ Domain model, handles epoch ↔ LocalDate |
| `AppModule.kt` | Hilt binds all repositories and DAOs |
| `HomeViewModel.kt` | Date navigation, daily summary, task list state |
| `TaskViewModel.kt` | Add/edit form state, validation, reminder scheduling |
| `ReminderScheduler.kt` | Schedules/cancels AlarmManager exact alarms |
| `ReminderReceiver.kt` | Fires notification when alarm triggers |
| `nav_graph.xml` | All screen destinations and transitions |
| `colors.xml` | Full pastel color palette |
| `themes.xml` | Material3 theme wired to pastel colors |
