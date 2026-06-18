package com.puneeth.aiinterviewcoach.data.local.seed

import com.puneeth.aiinterviewcoach.data.local.entity.QuestionEntity
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty

object QuestionSeedData {
    val questions: List<QuestionEntity> = listOf(
        QuestionEntity(
            category = InterviewCategory.KOTLIN.name,
            difficulty = InterviewDifficulty.JUNIOR.name,
            question = "What is the difference between val and var in Kotlin?",
            idealAnswer = "val is immutable after initialization, while var can be reassigned. Both may still reference mutable objects depending on the type.",
            bookmarked = false,
            tags = "kotlin,basics,immutability",
        ),
        QuestionEntity(
            category = InterviewCategory.COROUTINES.name,
            difficulty = InterviewDifficulty.MID.name,
            question = "Explain structured concurrency in Kotlin Coroutines.",
            idealAnswer = "Structured concurrency ties child coroutines to a parent scope so lifecycle, cancellation, and failure handling stay predictable.",
            bookmarked = false,
            tags = "coroutines,structured-concurrency",
        ),
        QuestionEntity(
            category = InterviewCategory.FLOW.name,
            difficulty = InterviewDifficulty.SENIOR.name,
            question = "When would you use SharedFlow instead of StateFlow?",
            idealAnswer = "Use SharedFlow for multicast event streams or hot flows without a single current state value. StateFlow is optimized for observable state.",
            bookmarked = false,
            tags = "flow,stateflow,sharedflow",
        ),
        QuestionEntity(
            category = InterviewCategory.JETPACK_COMPOSE.name,
            difficulty = InterviewDifficulty.MID.name,
            question = "How do recomposition and state hoisting improve Compose UIs?",
            idealAnswer = "Recomposition updates only affected parts of the UI, while state hoisting improves testability and reuse by moving state ownership upward.",
            bookmarked = false,
            tags = "compose,recomposition,state-hoisting",
        ),
        QuestionEntity(
            category = InterviewCategory.ANDROID_ARCHITECTURE.name,
            difficulty = InterviewDifficulty.SENIOR.name,
            question = "Why should repositories sit between ViewModels and data sources?",
            idealAnswer = "Repositories centralize data orchestration, hide implementation details, combine sources, and keep presentation logic independent from storage or network APIs.",
            bookmarked = false,
            tags = "architecture,mvvm,repository",
        ),
        QuestionEntity(
            category = InterviewCategory.HILT.name,
            difficulty = InterviewDifficulty.MID.name,
            question = "What problem does Hilt solve in Android apps?",
            idealAnswer = "Hilt standardizes dependency injection with generated components, scoped lifetimes, and Android entry points, reducing boilerplate and wiring errors.",
            bookmarked = false,
            tags = "hilt,di",
        ),
        QuestionEntity(
            category = InterviewCategory.ROOM.name,
            difficulty = InterviewDifficulty.MID.name,
            question = "How would you model a one-to-many relationship in Room?",
            idealAnswer = "Create separate entities with a foreign key and use data classes with @Relation to query nested results efficiently.",
            bookmarked = false,
            tags = "room,sql,relations",
        ),
        QuestionEntity(
            category = InterviewCategory.WORKMANAGER.name,
            difficulty = InterviewDifficulty.SENIOR.name,
            question = "When is WorkManager the right choice over a foreground service?",
            idealAnswer = "Use WorkManager for guaranteed, deferrable background work that must survive process death; use a foreground service for user-visible ongoing tasks.",
            bookmarked = false,
            tags = "workmanager,background",
        ),
        QuestionEntity(
            category = InterviewCategory.SYSTEM_DESIGN.name,
            difficulty = InterviewDifficulty.LEAD.name,
            question = "How would you design an offline-first interview practice app?",
            idealAnswer = "Prioritize local persistence, sync queues, conflict resolution, telemetry boundaries, and clear ownership between domain, storage, and network layers.",
            bookmarked = false,
            tags = "system-design,offline-first,scalability",
        ),
    )
}
