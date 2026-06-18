package com.puneeth.aiinterviewcoach.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth.aiinterviewcoach.domain.model.CategorySummary
import com.puneeth.aiinterviewcoach.domain.usecase.ObserveCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CategoriesUiState(
    val searchQuery: String = "",
    val categories: List<CategorySummary> = emptyList(),
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val observeCategories: ObserveCategoriesUseCase,
) : ViewModel() {
    private val query = MutableStateFlow("")

    val uiState: StateFlow<CategoriesUiState> = query
        .flatMapLatest { search ->
            observeCategories(search).map { categories ->
                CategoriesUiState(searchQuery = search, categories = categories)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CategoriesUiState())

    fun updateQuery(value: String) {
        query.value = value
    }
}
