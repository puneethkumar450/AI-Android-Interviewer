# AI Android Interview Coach

Version 1 focuses on three release features:

- Offline Question Bank with Room-backed search and bookmarks
- AI Mock Interview with Gemini prompt orchestration and adaptive follow-up questions
- Progress Tracking with stored interview results, weak-area detection, and learning suggestions

## Stack

- Kotlin
- Jetpack Compose + Material 3
- Hilt
- Navigation Compose
- Room
- DataStore
- Retrofit
- Coroutines + Flow
- MVVM clean architecture

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

- `Question Bank`
- `AI Mock`
- `Progress`

## Gemini Setup

Add your Gemini key to `local.properties`:

```properties
GEMINI_API_KEY=your_key_here
```

The app falls back to deterministic local interview behavior when the key is missing, which keeps development and offline demos usable.

## Next Release Ideas

- Dedicated interview practice drills
- Exportable session reports
- Multi-module extraction into `core`, `feature`, and `sync` modules
