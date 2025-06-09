package com.example.quiz_app.domain

data class SearchCriteria(
    val query: String = "",
    val categories: List<String> = emptyList(),
    val difficulties: List<QuizDifficulty> = emptyList(),
    val chapters: List<String> = emptyList(),
    val questionCount: IntRange? = null,
    val timeLimit: IntRange? = null,
    val isBookmarked: Boolean? = null,
    val isCompleted: Boolean? = null,
    val sortBy: SortOption = SortOption.RELEVANCE,
    val sortOrder: SortOrder = SortOrder.DESCENDING
)

enum class SortOption {
    RELEVANCE,
    TITLE,
    DIFFICULTY,
    CREATION_DATE,
    COMPLETION_RATE,
    QUESTION_COUNT,
    TIME_LIMIT
}

enum class SortOrder {
    ASCENDING,
    DESCENDING
}

data class SavedSearch(
    val id: String,
    val name: String,
    val criteria: SearchCriteria,
    val createdAt: java.time.LocalDateTime,
    val lastUsed: java.time.LocalDateTime,
    val useCount: Int
)

data class SearchResult(
    val quiz: Quiz,
    val relevanceScore: Float,
    val matchedFields: List<String>,
    val highlightedTitle: String,
    val highlightedDescription: String
)

data class FilterOptions(
    val availableCategories: List<String>,
    val availableChapters: List<String>,
    val difficultyRange: List<QuizDifficulty>,
    val questionCountRange: IntRange,
    val timeLimitRange: IntRange
)