# Version 1 Release Scope

## Product Goal

Create a completely offline Android interview preparation app with no AI APIs, backend services, or cloud storage.

## Included

- Home Screen
  - Total questions
  - Bookmarks count
  - Continue practice
  - Category shortcuts

- Categories Screen
  - All categories
  - Search categories
  - Question counts per category

- Questions Screen
  - One question at a time
  - Reveal answer
  - Explanation
  - Previous and next navigation
  - Bookmark toggle

- Search Screen
  - Search by question
  - Search by category
  - Search by tags

- Bookmarks Screen
  - Saved questions
  - Remove bookmark
  - Export bookmarked questions to local JSON

- Progress Screen
  - Viewed questions
  - Completed questions
  - Category completion
  - Difficulty completion
  - Daily streak

- Settings Screen
  - Dark mode
  - Dynamic color
  - Reset progress

- Data Layer
  - Room database
  - DataStore preferences
  - JSON asset pack import on first launch
  - Multi-pack support from `assets/question_packs`

## Deferred For Later Releases

- Pack download/update management
- Rich analytics charts
- Spaced repetition and revision plans
- Multi-module physical split into `core`, `feature`, and `sync` modules
