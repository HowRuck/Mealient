package gq.kirmanak.mealient.data.recipes.network

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class RecipeSummaryInfo(
    val remoteId: String,
    val name: String,
    val slug: String,
    val description: String = "",
    val imageId: String,
    val dateAdded: LocalDate,
    val dateUpdated: LocalDateTime
)
