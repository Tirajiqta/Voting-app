package com.example.android.ui.theme.viewmodels

import com.example.android.dao.election.CandidateVoteResult
import com.example.android.dao.election.PartyVoteResult


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
// Import the specific data classes used for results (ensure these a
// Import your Repository
import com.example.android.repository.ElectionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException // Needed for Factory exception

// Define the UI State data class for this screen
data class ResultsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val partyResults: List<PartyVoteResult> = emptyList(),      // List contains names from DB query
    val candidateResults: List<CandidateVoteResult> = emptyList(), // List contains names from DB query
    val electionName: String = "Зареждане..." // Start with a loading message
)

// --- ViewModel Implementation ---

class ResultsViewModel(
    private val electionsRepository: ElectionsRepository,
    private val savedStateHandle: SavedStateHandle // Inject handle

    // If you pass electionId via navigation arguments, inject SavedStateHandle here:
    // private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    private var currentElectionId: Long = -1L // Initialize

    init {
        val electionIdArg: Long? = savedStateHandle["electionId"] // Key matches navArgument name
        if (electionIdArg != null && electionIdArg > 0L) {
            currentElectionId = electionIdArg
            loadResults()
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Грешка: Невалиден ID на избор.") }
        }
    }
    // private var currentElectionId: Long = -1L // Initialize if using SavedStateHandle

    // --- Load results on initialization (if using hardcoded ID) ---
    init {
        if (currentElectionId != -1L) { // Check if ID is valid (relevant if using SavedStateHandle)
            loadResults()
        } else if (currentElectionId == 1L) { // Trigger load for hardcoded ID
            loadResults()
        }
        else { // Handle case where ID is not set (if using SavedStateHandle)
            _uiState.update { it.copy(isLoading = false, error = "Грешка: Не е посочен избор за резултати.") }
        }
    }


    // --- Function to Load Results ---
    fun loadResults() {
        // Prevent loading if ID is invalid (mainly for SavedStateHandle approach)
        if (currentElectionId <= 0L) {
            Log.w("ResultsViewModel", "loadResults called with invalid electionId: $currentElectionId")
            _uiState.update { it.copy(isLoading = false, error = "Невалиден избор.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Fetch results including names from the repository
                // These repository methods now call the DAO functions with JOINs
                val partyVotes = electionsRepository.getPartyResults(currentElectionId)
                val candidateVotes = electionsRepository.getCandidateResults(currentElectionId)

                // TODO: Fetch the actual election name based on currentElectionId
                // val electionDetails = electionsRepository.getElectionDetails(currentElectionId) // Example call
                val fetchedElectionName = "Резултати за Избор #$currentElectionId" // Placeholder name

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        partyResults = partyVotes,      // Assign results with names
                        candidateResults = candidateVotes,  // Assign results with names
                        electionName = fetchedElectionName // Use fetched name
                    )
                }
            } catch (e: Exception) {
                Log.e("ResultsViewModel", "Error loading results for election $currentElectionId", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Грешка при зареждане на резултати: ${e.message}")
                }
            }
        }
    }

    // Optional: Function to clear error messages if needed
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}


// --- ViewModel Factory ---
// Needed because ResultsViewModel depends on ElectionsRepository
class ResultsViewModelFactory(
    private val electionsRepository: ElectionsRepository,
    private val savedStateHandle: SavedStateHandle // Add handle here

    // Add SavedStateHandle here if you use it in the ViewModel constructor
    // private val savedStateHandle: SavedStateHandle
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Pass SavedStateHandle here if needed:
            // return ResultsViewModel(electionsRepository, savedStateHandle) as T
            return ResultsViewModel(electionsRepository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}