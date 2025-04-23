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

data class PartyDisplayItem(
    val id: Long,
    val number: Int,
    val name: String
)

data class CandidateDisplayItem(
    val id: Long,
    val preferenceNumber: Int
)

// UI State for the VotingScreen
data class VotingUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val electionName: String = "",
    val parties: List<PartyDisplayItem> = emptyList(),
    val candidatesForSelectedParty: List<CandidateDisplayItem> = emptyList(),
    val selectedPartyId: Long? = null,
    val selectedCandidateId: Long? = null,
    val voteSavedSuccessfully: Boolean = false
)

class VotingViewModel(
    private val electionsRepository: ElectionsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(VotingUiState())
    val uiState: StateFlow<VotingUiState> = _uiState.asStateFlow()

    private val electionId: Long = checkNotNull(savedStateHandle["electionId"])

    private var rawElectionData: Any? = null

    init {
        loadElectionDetails()
    }

    private fun loadElectionDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val electionDetails = getMockElectionDetails(electionId)

                rawElectionData = electionDetails // Store raw data

                val partyItems = electionDetails.parties.mapIndexed { index, party ->
                    PartyDisplayItem(
                        id = party.id,
                        number = index + 1,
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
        val selectedPartyData = getMockElectionDetails(electionId)
            .parties.find { it.id == partyId }

        val candidateItems = selectedPartyData?.candidates?.mapIndexed { index, candidate ->
            CandidateDisplayItem(
                id = candidate.id,
                preferenceNumber = 101 + index
            )
        } ?: emptyList()

        _uiState.update {
            it.copy(
                selectedPartyId = partyId,
                selectedCandidateId = null,
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
    fun saveVote(userId: Long) {
        val currentState = _uiState.value
        if (currentState.selectedPartyId == null || currentState.selectedCandidateId == null) {
            _uiState.update { it.copy(error = "Моля, изберете партия и преференция.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val vote = VoteEntity(
                    userId = userId,
                    electionId = electionId,
                    partyId = currentState.selectedPartyId,
                    candidateId = currentState.selectedCandidateId,
                    voteTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                )
                electionsRepository.insertVote(vote)
                _uiState.update { it.copy(isLoading = false, voteSavedSuccessfully = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Грешка при запис на гласа: ${e.message}")
                }
            }
        }
    }

    private data class MockElectionDetails(val id: Long, val electionName: String, val parties: List<MockParty>)
    private data class MockParty(val id: Long, val name: String, val candidates: List<MockCandidate>)
    private data class MockCandidate(val id: Long, val name: String)

    private fun getMockElectionDetails(id: Long): MockElectionDetails {
        val parties = listOf(
            MockParty(1, "ДОСТ", List(3) { MockCandidate(10L + it, "Кандидат ${10L + it}") }),
            MockParty(2, "Глас народен", List(5) { MockCandidate(20L + it, "Кандидат ${20L + it}") }),
            MockParty(3, "Социалистическа партия „Български път“", List(12) { MockCandidate(30L + it, "Кандидат ${30L + it}") }), // Party 3 has 12 candidates -> Prefs 101-112
            MockParty(4, "Величие", List(2) { MockCandidate(40L + it, "Кандидат ${40L + it}") }),
            MockParty(5, "Булгари", List(4) { MockCandidate(50L + it, "Кандидат ${50L + it}") }),
            MockParty(6, "Коалиция „Моя страна България“", List(6) { MockCandidate(60L + it, "Кандидат ${60L + it}") }),
            MockParty(7, "Има такъв народ", List(8) { MockCandidate(70L + it, "Кандидат ${70L + it}") }),
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