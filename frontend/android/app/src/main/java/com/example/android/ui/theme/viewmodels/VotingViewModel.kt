package com.example.android.ui.theme.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.android.repository.ElectionsRepository
import androidx.lifecycle.viewModelScope
import com.example.android.entity.election.VoteEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Represents a party shown in the list
data class PartyDisplayItem(
    val id: Long,
    val number: Int, // The number displayed next to the party (1, 2, 3...)
    val name: String
)

// Represents a candidate preference shown in the grid
data class CandidateDisplayItem(
    val id: Long, // The actual Candidate ID from the database/API
    val preferenceNumber: Int // The number displayed in the circle (101, 102...)
)

// UI State for the VotingScreen
data class VotingUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val electionName: String = "",
    val parties: List<PartyDisplayItem> = emptyList(),
    val candidatesForSelectedParty: List<CandidateDisplayItem> = emptyList(),
    val selectedPartyId: Long? = null,
    val selectedCandidateId: Long? = null, // Store the actual candidate ID
    val voteSavedSuccessfully: Boolean = false // To trigger navigation back
)

class VotingViewModel(
    private val electionsRepository: ElectionsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(VotingUiState())
    val uiState: StateFlow<VotingUiState> = _uiState.asStateFlow()

    // Assuming electionId is passed via navigation arguments
    private val electionId: Long = checkNotNull(savedStateHandle["electionId"])

    // Keep the raw data fetched from the repository
    // Replace 'Any' with your actual data structure from the repository
    private var rawElectionData: /* YourElectionDetailType? */ Any? = null // TODO: Replace Any

    init {
        loadElectionDetails()
    }

    private fun loadElectionDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // TODO: Replace with your actual repository call and data class
                // val electionDetails = electionsRepository.getElectionWithPartiesAndCandidates(electionId)
                // MOCK DATA - REPLACE WITH ACTUAL REPOSITORY CALL
                val electionDetails = getMockElectionDetails(electionId) // Replace with real call

                rawElectionData = electionDetails // Store raw data

                // Map to Display Items
                val partyItems = electionDetails.parties.mapIndexed { index, party ->
                    PartyDisplayItem(
                        id = party.id,
                        number = index + 1, // Assign numbers 1, 2, 3...
                        name = party.name
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        electionName = electionDetails.electionName,
                        parties = partyItems
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Грешка при зареждане на данни: ${e.message}")
                }
            }
        }
    }

    fun selectParty(partyId: Long) {
        val selectedPartyData = getMockElectionDetails(electionId) // Replace with real lookup
            .parties.find { it.id == partyId }

        val candidateItems = selectedPartyData?.candidates?.mapIndexed { index, candidate ->
            CandidateDisplayItem(
                id = candidate.id, // The actual candidate ID
                preferenceNumber = 101 + index // Generate preference numbers 101, 102...
            )
        } ?: emptyList()

        _uiState.update {
            it.copy(
                selectedPartyId = partyId,
                selectedCandidateId = null, // Reset candidate selection when party changes
                candidatesForSelectedParty = candidateItems
            )
        }
    }

    fun selectCandidate(candidateId: Long) {
        _uiState.update {
            it.copy(selectedCandidateId = candidateId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveVote(userId: Long) { // Pass the userId here
        val currentState = _uiState.value
        if (currentState.selectedPartyId == null || currentState.selectedCandidateId == null) {
            _uiState.update { it.copy(error = "Моля, изберете партия и преференция.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) } // Show loading during save
            try {
                val vote = VoteEntity(
                    userId = userId, // Use the passed userId
                    electionId = electionId,
                    partyId = currentState.selectedPartyId,
                    candidateId = currentState.selectedCandidateId, // This is the preference
                    voteTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                )
                electionsRepository.insertVote(vote) // Save to DB
                _uiState.update { it.copy(isLoading = false, voteSavedSuccessfully = true) } // Signal success
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Грешка при запис на гласа: ${e.message}")
                }
            }
        }
    }

    // --- MOCK DATA FUNCTION - REMOVE AND REPLACE WITH REPOSITORY ---
    // TODO: Define these data classes based on your actual API/DB structure
    private data class MockElectionDetails(val id: Long, val electionName: String, val parties: List<MockParty>)
    private data class MockParty(val id: Long, val name: String, val candidates: List<MockCandidate>)
    private data class MockCandidate(val id: Long, val name: String)

    private fun getMockElectionDetails(id: Long): MockElectionDetails {
        // Create realistic mock data based on the Bulgarian example image if possible
        val parties = listOf(
            MockParty(1, "ДОСТ", List(3) { MockCandidate(10L + it, "Кандидат ${10L + it}") }),
            MockParty(2, "Глас народен", List(5) { MockCandidate(20L + it, "Кандидат ${20L + it}") }),
            MockParty(3, "Социалистическа партия „Български път“", List(12) { MockCandidate(30L + it, "Кандидат ${30L + it}") }), // Party 3 has 12 candidates -> Prefs 101-112
            MockParty(4, "Величие", List(2) { MockCandidate(40L + it, "Кандидат ${40L + it}") }),
            MockParty(5, "Булгари", List(4) { MockCandidate(50L + it, "Кандидат ${50L + it}") }),
            MockParty(6, "Коалиция „Моя страна България“", List(6) { MockCandidate(60L + it, "Кандидат ${60L + it}") }),
            MockParty(7, "Има такъв народ", List(8) { MockCandidate(70L + it, "Кандидат ${70L + it}") }),
            // Add more parties based on the image...
        )
        return MockElectionDetails(id, "Примерни Избори $id", parties)
    }
    // --- END MOCK DATA ---
}

// --- ViewModel Factory ---
// You'll need a factory if your ViewModel has dependencies
class VotingViewModelFactory(
    private val electionsRepository: ElectionsRepository,
    private val savedStateHandle: SavedStateHandle
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VotingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VotingViewModel(electionsRepository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}