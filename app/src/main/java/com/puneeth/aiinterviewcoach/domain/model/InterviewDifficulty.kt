package com.puneeth.aiinterviewcoach.domain.model

enum class InterviewDifficulty(val title: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced"),
    EXPERT("Expert");

    companion object {
        fun fromTitle(title: String): InterviewDifficulty {
            return entries.firstOrNull { it.title.equals(title, ignoreCase = true) }
                ?: valueOf(title.uppercase())
        }
    }
}
