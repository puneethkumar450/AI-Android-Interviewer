# Android Interview Preparation

Version 1 is a completely offline Android interview preparation app.

## Included in V1

- Home dashboard with total questions, bookmarks, continue practice, and category shortcuts
- Room-backed Question Bank imported from JSON asset packs on first launch
- Category browsing with question counts and search
- One-question-at-a-time practice flow with reveal answer, explanation, bookmark, next, and previous actions
- Global search by question, category, answer, explanation, and tags
- Bookmarks screen with local export support
- Progress tracking for viewed questions, completed questions, category progress, difficulty progress, continue practice, and daily streak
- Settings for dark mode, dynamic color, reset progress, and bookmark export

## Stack

- Kotlin
- Jetpack Compose + Material 3
- Hilt
- Navigation Compose
- Room
- DataStore
- Coroutines + Flow
- MVVM clean architecture

## Offline Data Strategy

- JSON packs live under `app/src/main/assets/question_packs`
- All `.json` files in that folder are imported automatically on first launch
- New question packs can be added later without changing importer code
- The architecture is ready for large local datasets; scaling beyond 1000 questions is a content concern, not a code change

## Project Structure

```text
app/src/main/java/com/puneeth/aiinterviewcoach
├── data
│   ├── local
│   ├── preferences
│   ├── remote
│   └── repository
├── di
├── domain
│   ├── model
│   ├── repository
│   └── usecase
└── presentation
    ├── navigation
    ├── screen
    ├── theme
    └── viewmodel
```

## V1 Navigation

- `Home`
- `Categories`
- `Progress`
- `Search`
- `Bookmarks`
- `Settings`

## Next Release Ideas

- Rich analytics dashboards
- Pack update management
- Study plans and spaced repetition
- Multi-module extraction into `core`, `feature`, and `sync` modules
