package com.example.android.ui.theme.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.repository.ElectionsRepository
import androidx.lifecycle.viewModelScope
import com.example.android.api.VotingApi
import com.example.android.api.VotingApi.Callback
import com.example.android.dto.response.LoginResponse
import com.example.android.dto.response.elections.ElectionResponseDTO
import com.example.android.dummymodel.Candidate
import com.example.android.dummymodel.Party
import com.example.android.entity.election.VoteEntity
import com.example.android.utils.CurrentUserHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// Represents a party shown in the list

// UI State for the VotingScreen
data class VotingUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val electionTitle: String = "", // Added field
    val electionDate: String = "",  // Added field (format as needed)
    val parties: List<Party> = emptyList(), // Use dummy model type
    val candidates: List<Candidate> = emptyList(), // Use dummy model type (all candidates)
    val voteSavedSuccessfully: Boolean = false,
    val voteToPreview: Boolean = false
    // selectedPartyId and selectedCandidateId are managed *within* ParliamentVoteScreen now
    // The ViewModel will receive the final selection via the onReviewVote callback
)
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

@RequiresApi(Build.VERSION_CODES.O)
class VotingViewModel(
    private val electionsRepository: ElectionsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(VotingUiState())
    val uiState: StateFlow<VotingUiState> = _uiState.asStateFlow()

    private val electionId: Long = checkNotNull(savedStateHandle["electionId"])

    init {
        loadElectionDetails()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadElectionDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // --- Replace with your ACTUAL repository call ---
                // Example: Fetch detailed election data including all associated parties and candidates
                // val electionDetails = electionsRepository.getElectionWithDetails(electionId)
                val electionDetails = getMockElectionDetails(electionId)

//                var electionDetails: ElectionResponseDTO? = null // Replace with REAL call
//                VotingApi.getElection(electionId, object : Callback<ElectionResponseDTO> {
//                    override fun onSuccess(response: ElectionResponseDTO) {
//                        // Resume the coroutine with the successful result
//                        // Check if the coroutine is still active before resuming
//                        electionDetails = response
//                    }
//
//                    override fun onFailure(error: Throwable) {
//                        // Resume the coroutine with the exception
//                        // Check if the coroutine is still active before resuming
//                        Log.e("Model", "error = " + error.message)
//                    }
//                })
                val partyItems = electionDetails?.parties?.map { repoParty ->
                    // !! Conversion Warning: Long to Int !!
                    if (repoParty.id > Int.MAX_VALUE || repoParty.id < Int.MIN_VALUE) {
                        // Handle error - ID out of range for Int
                        throw IllegalStateException("Party ID ${repoParty.id} cannot be safely converted to Int.")
                    }
                    Party(
                        id = repoParty.id.toInt(), // Convert Long to Int
                        name = repoParty.name
                    )
                }


                val candidateItems = electionDetails?.candidates?.map { repoCandidate ->
                    // !! Conversion Warning: Long to Int !!
                    if (repoCandidate.id > Int.MAX_VALUE || repoCandidate.id < Int.MIN_VALUE ||
                        (repoCandidate.partyId
                            ?: 0) > Int.MAX_VALUE || (repoCandidate.partyId ?: 0) < Int.MIN_VALUE
                    ) {
                        throw IllegalStateException("Candidate/Party ID ${repoCandidate.id}/${repoCandidate.partyId} cannot be safely converted to Int.")
                    }
                    Candidate(
                        // Use the Candidate's actual ID (or preference number if that's what the API gives)
                        // Assuming repoCandidate.id IS the preference number shown in the circle
                        id = repoCandidate.id, // Use actual Candidate ID as preference ID shown
                        name = repoCandidate.name, // Pass name along
                        partyId = repoCandidate.partyId?.toInt() ?: 0,
                        preferenceNumber = repoCandidate.id.toInt() // Convert Long to Int
                    )
                }

                // Format Date (Example)
                val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) // Adjust format as needed
                val formattedDate = try {
                    LocalDate.parse(electionDetails?.endDate).format(dateFormatter)
                } catch (e: Exception) {
                    electionDetails?.endDate // Fallback to original string if parsing fails
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        electionTitle = electionDetails?.electionName ?: "",
                        electionDate = formattedDate ?: "", // Use formatted date
                        parties = partyItems?: emptyList(),
                        candidates = candidateItems ?: emptyList() // Pass ALL candidates
                    )
                }
            } catch (e: Exception) {
                println("Error loading election details: ${e.message}") // Log error
                _uiState.update {
                    it.copy(isLoading = false, error = "Грешка при зареждане на данни: ${e.localizedMessage}")
                }
            }
        }
    }

    // Selection is handled within ParliamentVoteScreen, no specific ViewModel functions needed for it.

    // Updated saveVote to accept Int IDs from the screen
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveVote(userId: Long, selectedPartyIntId: Int, selectedCandidateIntId: Int?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) } // Show loading during save
            try {
                // Convert IDs back to Long for the database entity
                val selectedPartyLongId = selectedPartyIntId.toLong()
                val selectedCandidateLongId = selectedCandidateIntId?.toLong() // Keep nullability
                val vote = VoteEntity(
                    userId = userId,
                    electionId = electionId, // The original electionId from savedStateHandle
                    partyId = selectedPartyLongId,
                    // Use the *actual candidate ID* if selectedCandidateIntId represents it.
                    // If selectedCandidateIntId represents a preference number, you might need
                    // to look up the actual candidate ID based on partyId and preference number.
                    // Assuming selectedCandidateIntId *is* the candidate ID for simplicity here.
                    candidateId = selectedCandidateLongId,
                    voteTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                )
                Log.d("VOTE_SAVE_DEBUG", "Attempting to save vote: electionId=${vote.electionId}, partyId=${vote.partyId}, candidateId=${vote.candidateId}, userId=${vote.userId}")
                val userId = CurrentUserHolder.getCurrentProfile()?.user?.id ?: 1;
                electionsRepository.insertVoteAndCastApiVote(userId, vote.electionId, vote.partyId, vote.candidateId)

                _uiState.update { it.copy(isLoading = false, voteSavedSuccessfully = true) } // Signal success
            } catch (e: Exception) {
                println("Error saving vote: ${e.message}") // Log error
                _uiState.update {
                    it.copy(isLoading = false, error = "Грешка при запис на гласа: ${e.localizedMessage}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // --- MOCK DATA FUNCTION - REMOVE AND REPLACE WITH REPOSITORY ---
    // TODO: Define these data classes based on your actual API/DB structure
    private data class MockElectionDetails(val id: Long, val electionName: String, val endDate: String, val parties: List<MockParty>, val candidates: List<MockCandidate>)
    private data class MockParty(val id: Long, val name: String)
    private data class MockCandidate(val id: Long, val name: String, val partyId: Long) // Added partyId

    private fun getMockElectionDetails(id: Long): MockElectionDetails {
        val parties = List(7) { index ->
            MockParty(id = index + 1L, name = "Партия / Коалиция ${index + 1}")
        }.plus(MockParty(id = 8L, name = "Не подкрепям никого"))

        // Create mock candidates associated with parties
        val candidates = mutableListOf<MockCandidate>()
        var candidateCounter = 101L
        parties.filter{it.id != 8L}.forEach { party -> // Add candidates for all except "Не подкрепям"
            val numCandidates = (2..6).random() // Random number of candidates per party
            repeat(numCandidates) {
                candidates.add(MockCandidate(id = candidateCounter++, name = "Кандидат ${candidateCounter-1}", partyId = party.id))
            }
        }

        return MockElectionDetails(id, "Избори за НС $id", "2024-10-27", parties, candidates)
    }
    // --- END MOCK DATA ---
}

// --- ViewModel Factory ---
// You'll need a factory if your ViewModel has dependencies
class VotingViewModelFactory(
    private val electionsRepository: ElectionsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VotingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VotingViewModel(electionsRepository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}