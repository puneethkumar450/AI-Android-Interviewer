package com.puneeth.aiinterviewcoach.domain.model

enum class InterviewCategory(val title: String) {
    KOTLIN("Kotlin"),
    COROUTINES("Coroutines"),
    FLOW("Flow"),
    JETPACK_COMPOSE("Jetpack Compose"),
    ANDROID_ARCHITECTURE("Android Architecture"),
    HILT("Hilt"),
    ROOM("Room"),
    WORKMANAGER("WorkManager"),
    ANDROID_PERFORMANCE("Android Performance"),
    ANDROID_TESTING("Android Testing"),
    SYSTEM_DESIGN("System Design"),
    SENIOR_ANDROID("Senior Android");

    companion object {
        fun fromTitle(title: String): InterviewCategory {
            return entries.firstOrNull { it.title.equals(title, ignoreCase = true) }
                ?: valueOf(title.replace(" ", "_").uppercase())
        }
    }
}
