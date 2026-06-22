package com.puneeth.aiinterviewcoach.domain.model

enum class ConfidenceRating(val label: String) {
    EASY("Easy"),
    OKAY("Okay"),
    HARD("Hard"),
    ;

    companion object {
        fun fromString(value: String?): ConfidenceRating? =
            entries.firstOrNull { it.name == value }
    }
}
