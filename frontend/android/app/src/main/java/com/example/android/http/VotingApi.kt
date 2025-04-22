package com.example.android.api

import com.example.android.dto.request.*
import com.example.android.dto.response.*
import com.example.android.dto.response.elections.*
import com.example.android.dto.response.referendum.*
import com.example.android.dto.response.survey.SurveyDTO
import com.example.android.dto.response.survey.SurveyOptionDTO
import com.example.android.dto.response.survey.SurveyQuestionDTO
import com.example.android.dto.response.survey.SurveyResponseDTO
import com.example.android.dto.response.survey.SurveyResultsDTO
import com.example.android.utils.InMemoryTokenHolder

object VotingApi {

    private const val BASE_URL = "https://desktop-4pa1111.tail83a47.ts.net:8443/api"

    fun login(request: LoginRequest, callback: Callback<LoginResponse>) {
        ApiClient.post<LoginRequest, LoginResponse>(
            "$BASE_URL/users/login",
            request,
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }
    //added
    suspend fun loginSuspending(request: LoginRequest): LoginResponse {
        // Use suspendCancellableCoroutine to bridge callback to coroutine
        return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            // Call the original callback-based function
            login(request, object : Callback<LoginResponse> {
                override fun onSuccess(response: LoginResponse) {
                    // Resume the coroutine with the successful result
                    // Check if the coroutine is still active before resuming
                    if (continuation.isActive) {
                        continuation.resume(response) { /* Handle potential resume error if needed */ }
                    }
                }

                override fun onFailure(error: Throwable) {
                    // Resume the coroutine with the exception
                    // Check if the coroutine is still active before resuming
                    if (continuation.isActive) {
                        continuation.resumeWith(Result.failure(error))
                    }
                }
            })

            // Optional: Handle coroutine cancellation if your ApiClient supports request cancellation
            continuation.invokeOnCancellation {
                // Example: If ApiClient had a way to cancel calls associated with a tag/token
                // ApiClient.cancelRequest("login_${request.hashCode()}")
                println("Login request coroutine cancelled")
            }
        }
    }


    fun createElection(request: ElectionsRequestDTO, callback: Callback<ElectionResponseDTO>) {
        ApiClient.post<ElectionsRequestDTO, ElectionResponseDTO>(
            "$BASE_URL/elections",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun updateElection(id: Long, request: ElectionsRequestDTO, callback: Callback<ElectionResponseDTO>) {
        ApiClient.post<ElectionsRequestDTO, ElectionResponseDTO>(
            "$BASE_URL/elections/$id",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun deleteElection(id: Long, callback: Callback<Unit>) {
        ApiClient.get<Unit>(
            "$BASE_URL/elections/$id",
            headers = getAuthHeaders(),
            onSuccess = { callback.onSuccess(Unit) },
            onFailure = callback::onFailure
        )
    }

    fun getElection(id: Long, callback: Callback<ElectionResponseDTO>) {
        ApiClient.get<ElectionResponseDTO>(
            "$BASE_URL/elections/$id",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun listElections(page: Int, size: Int, status: String?, type: String?, callback: Callback<PagedResponseDTO<ElectionResponseDTO>>) {
        val url = buildString {
            append("$BASE_URL/elections/list-elections")
        }
        ApiClient.get<PagedResponseDTO<ElectionResponseDTO>>(
            url,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun addCandidate(electionId: Long, request: CandidateRequestDTO, callback: Callback<CandidateResponseDTO>) {
        ApiClient.post<CandidateRequestDTO, CandidateResponseDTO>(
            "$BASE_URL/elections/$electionId/candidates",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun updateCandidate(id: Long, request: CandidateRequestDTO, callback: Callback<CandidateResponseDTO>) {
        ApiClient.post<CandidateRequestDTO, CandidateResponseDTO>(
            "$BASE_URL/candidates/$id",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun deleteCandidate(id: Long, callback: Callback<Unit>) {

        ApiClient.get<Unit>(
            "$BASE_URL/candidates/$id",
            headers = getAuthHeaders(),
            onSuccess = { callback.onSuccess(Unit) },
            onFailure = callback::onFailure
        )
    }

    fun getUserProfileDetails(callback: Callback<UserProfileDetailsDTO>) {
        // Construct the specific URL for the endpoint
        val url = "$BASE_URL/users/me"
        // Make the GET request using the generic ApiClient
        ApiClient.get<UserProfileDetailsDTO>(
            url = url,
            headers = getAuthHeaders(),
            onSuccess = { userProfileDetails ->
                // Successfully received and parsed the UserProfileDetailsDTO
                callback.onSuccess(userProfileDetails)
            },
            onFailure = { error ->
                // An error occurred (network, server error, parsing error, etc.)
                callback.onFailure(error)
            }
            // Assumes ApiClient.get() automatically adds necessary headers
            // like Authorization Bearer token from a saved state.
        )
    }

    fun castVote(request: VoteRequestDTO, callback: Callback<VoteResponseDTO>) {
        ApiClient.post<VoteRequestDTO, VoteResponseDTO>(
            "$BASE_URL/votes",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun getElectionResults(electionId: Long, callback: Callback<ElectionResultsDTO>) {
        ApiClient.get<ElectionResultsDTO>(
            "$BASE_URL/elections/$electionId/results",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun listRegions(callback: Callback<List<RegionResponseDTO>>) {
        ApiClient.get(
            "$BASE_URL/position/regions",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun getRegion(id: Long, callback: Callback<RegionResponseDTO>) {
        ApiClient.get(
            "$BASE_URL/position/regions/$id",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun listMunicipalities(callback: Callback<List<MunicipalityResponseDTO>>) {
        ApiClient.get(
            "$BASE_URL/position/municipalities",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun getMunicipality(id: Long, callback: Callback<MunicipalityResponseDTO>) {
        ApiClient.get(
            "$BASE_URL/position/municipalities/$id",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun listLocations(callback: Callback<List<LocationResponseDTO>>) {
        ApiClient.get(
            "$BASE_URL/position/locations",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun getLocation(id: Long, callback: Callback<LocationResponseDTO>) {
        ApiClient.get(
            "$BASE_URL/position/locations/$id",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun listLocationRegions(callback: Callback<List<LocationRegionResponseDTO>>) {
        ApiClient.get(
            "$BASE_URL/position/location-regions",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun getLocationRegion(id: Long, callback: Callback<LocationRegionResponseDTO>) {
        ApiClient.get(
            "$BASE_URL/position/location-regions/$id",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun createReferendum(request: ReferendumRequestDTO, callback: Callback<ReferendumResponseDTO>) {
        ApiClient.post<ReferendumRequestDTO, ReferendumResponseDTO>(
            "$BASE_URL/referendums",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun updateReferendum(id: Long, request: ReferendumRequestDTO, callback: Callback<ReferendumResponseDTO>) {
        ApiClient.post<ReferendumRequestDTO, ReferendumResponseDTO>(
            "$BASE_URL/referendums/$id",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun deleteReferendum(id: Long, callback: Callback<Unit>) {
        ApiClient.get<Unit>(
            "$BASE_URL/referendums/$id",
            headers = getAuthHeaders(),
            onSuccess = { callback.onSuccess(Unit) },
            onFailure = callback::onFailure
        )
    }

    fun getReferendum(id: Long, callback: Callback<ReferendumResponseDTO>) {
        ApiClient.get<ReferendumResponseDTO>(
            "$BASE_URL/referendums/$id",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun listReferendums(page: Int, size: Int, status: String?, callback: Callback<PagedResponseDTO<ReferendumResponseDTO>>) {
        val url = buildString {
            append("$BASE_URL/referendums?page=$page&size=$size")
            status?.let { append("&status=$it") }
        }
        ApiClient.get<PagedResponseDTO<ReferendumResponseDTO>>(
            url,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun castReferendumVote(id: Long, request: ReferendumVoteRequestDTO, callback: Callback<ReferendumVoteResponseDTO>) {
        ApiClient.post<ReferendumVoteRequestDTO, ReferendumVoteResponseDTO>(
            "$BASE_URL/referendums/$id/votes",
            request,
            headers = getAuthHeaders(),
            onSuccess = { callback.onSuccess(it) },
            onFailure = { callback.onFailure(it) }
        )
    }

    fun getReferendumResults(id: Long, callback: Callback<ReferendumResultsDTO>) {
        ApiClient.get<ReferendumResultsDTO>(
            "$BASE_URL/referendums/$id/results",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }


    fun updateSurvey(id: Long, request: SurveyRequestDTO, callback: Callback<SurveyDTO>) {
        ApiClient.post<SurveyRequestDTO, SurveyDTO>(
            "$BASE_URL/surveys/$id",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun deleteSurvey(id: Long, callback: Callback<Unit>) {
        ApiClient.get<Unit>(
            "$BASE_URL/surveys/$id",
            headers = getAuthHeaders(),
            onSuccess = { callback.onSuccess(Unit) },
            onFailure = callback::onFailure
        )
    }

    fun getSurvey(id: Long, callback: Callback<SurveyDTO>) {
        ApiClient.get<SurveyDTO>(
            "$BASE_URL/surveys/$id",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun listSurveys(page: Int, size: Int, status: String?, callback: Callback<PagedResponseDTO<SurveyDTO>>) {
        val url = buildString {
            append("$BASE_URL/surveys?page=$page&size=$size")
            status?.let { append("&status=$it") }
        }
        ApiClient.get<PagedResponseDTO<SurveyDTO>>(
            url,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun addSurveyQuestion(surveyId: Long, request: SurveyQuestionRequestDTO, callback: Callback<SurveyQuestionDTO>) {
        ApiClient.post<SurveyQuestionRequestDTO, SurveyQuestionDTO>(
            "$BASE_URL/surveys/$surveyId/questions",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun updateSurveyQuestion(surveyId: Long, questionId: Long, request: SurveyQuestionRequestDTO, callback: Callback<SurveyQuestionDTO>) {
        ApiClient.post<SurveyQuestionRequestDTO, SurveyQuestionDTO>(
            "$BASE_URL/surveys/$surveyId/questions/$questionId",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun deleteSurveyQuestion(surveyId: Long, questionId: Long, callback: Callback<Unit>) {
        ApiClient.get<Unit>(
            "$BASE_URL/surveys/$surveyId/questions/$questionId",
            headers = getAuthHeaders(),
            onSuccess = { callback.onSuccess(Unit) },
            onFailure = callback::onFailure
        )
    }

    fun addSurveyOption(surveyId: Long, questionId: Long, request: SurveyOptionRequestDTO, callback: Callback<SurveyOptionDTO>) {
        ApiClient.post<SurveyOptionRequestDTO, SurveyOptionDTO>(
            "$BASE_URL/surveys/$surveyId/questions/$questionId/options",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun updateSurveyOption(surveyId: Long, questionId: Long, optionId: Long, request: SurveyOptionRequestDTO, callback: Callback<SurveyOptionDTO>) {
        ApiClient.post<SurveyOptionRequestDTO, SurveyOptionDTO>(
            "$BASE_URL/surveys/$surveyId/questions/$questionId/options/$optionId",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun deleteSurveyOption(surveyId: Long, questionId: Long, optionId: Long, callback: Callback<Unit>) {
        ApiClient.get<Unit>(
            "$BASE_URL/surveys/$surveyId/questions/$questionId/options/$optionId",
            headers = getAuthHeaders(),
            onSuccess = { callback.onSuccess(Unit) },
            onFailure = callback::onFailure
        )
    }

    fun submitSurveyResponse(surveyId: Long, request: SurveyResponseRequestDTO, callback: Callback<SurveyResponseDTO>) {
        ApiClient.post<SurveyResponseRequestDTO, SurveyResponseDTO>(
            "$BASE_URL/surveys/$surveyId/responses",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun getSurveyResults(surveyId: Long, callback: Callback<SurveyResultsDTO>) {
        ApiClient.get<SurveyResultsDTO>(
            "$BASE_URL/surveys/$surveyId/results",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }
    fun createSurvey(request: SurveyRequestDTO, callback: Callback<SurveyDTO>) {
        ApiClient.post<SurveyRequestDTO, SurveyDTO>(
            "$BASE_URL/surveys",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun getUserProfile(token: String, callback: Callback<UserDTO>) {
        ApiClient.get<UserDTO>(
            "$BASE_URL/users/profile",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun updateUserProfile(token: String, request: UserDTO, callback: Callback<UserDTO>) {
        ApiClient.post<UserDTO, UserDTO>(
            "$BASE_URL/users/profile",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun getUserDocument(token: String, callback: Callback<DocumentResponseDTO>) {
        ApiClient.get<DocumentResponseDTO>(
            "$BASE_URL/users/profile/document",
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    fun addDocument(token: String, request: DocumentRequestDTO, callback: Callback<DocumentResponseDTO>) {
        ApiClient.post<DocumentRequestDTO, DocumentResponseDTO>(
            "$BASE_URL/users/profile/document",
            request,
            headers = getAuthHeaders(),
            onSuccess = callback::onSuccess,
            onFailure = callback::onFailure
        )
    }

    private fun getAuthHeaders(): Map<String, String>{
        val token = InMemoryTokenHolder.getToken()

        val requestHeaders: Map<String, String>
        if (!token.isNullOrBlank()) {
            // If token exists, create the Authorization header
            requestHeaders = mapOf("Authorization" to "Bearer $token")
        } else {
            // If no token, send empty headers. The backend should return 401 Unauthorized.
            requestHeaders = emptyMap()
        }
        return requestHeaders
    }
    interface Callback<T> {
        fun onSuccess(response: T)
        fun onFailure(error: Throwable)
    }
}