package com.example.android.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.repository.ElectionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ElectionChoiceUiState(
    val isLoading: Boolean = false,
    val elections: List<ElectionDisplayItem> = emptyList(),
    val error: String? = null
)
data class ElectionDisplayItem(
    val id: Long,
    val name: String
)
// HiltViewModel is common if using Hilt
// @HiltViewModel
class ElectionChoiceViewModel(
    private val electionRepository: ElectionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ElectionChoiceUiState())
    val uiState: StateFlow<ElectionChoiceUiState> = _uiState.asStateFlow()

    init {
        loadElections()
    }

    fun loadElections() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) } // Start loading

            // Attempt to fetch from network and store
            val fetchResult = electionRepository.fetchAndStoreActiveElections()

            fetchResult.onSuccess { storedElections ->
                // Fetch successful, update UI with the (potentially updated) list from DB
                // Map Entity to DisplayItem
                val displayItems = storedElections.map { it.toDisplayItem() }
                _uiState.update {
                    it.copy(isLoading = false, elections = displayItems, error = null)
                }

            }.onFailure { exception ->
                // Fetch failed, show error. Optionally, try loading from DB cache.
                _uiState.update { it.copy(isLoading = false, error = "Failed to load elections: ${exception.message}") }
                // Optional: Load cached data even if fetch failed
                // loadElectionsFromCacheOnError()
            }
        }
    }

    // Optional: Function to load data only from cache if network fails
    private suspend fun loadElectionsFromCacheOnError() {
        val cachedElections = electionRepository.getAllElectionsFromDb()
        val displayItems = cachedElections
            // Optional filter: .filter { it.status == "ACTIVE" } // If needed
            .map { it.toDisplayItem() }
        _uiState.update { it.copy(elections = displayItems) } // Update list, keep error message
    }

}