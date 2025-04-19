package com.example.android.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.dummymodel.* // Import your models (Party, Candidate, etc.)
import com.example.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotePreviewScreen(
    voteSummary: VoteSelectionSummary, // Contains the IDs selected by the user
    // --- Data needed to display names/text for the selections ---
    // Pass only the relevant lists based on which elections were active
    parliamentParties: List<Party> = emptyList(),
    parliamentCandidates: List<Candidate> = emptyList(),
    presidentialOptions: List<PresidentialPair> = emptyList(),
    referendumAnswers: List<ReferendumAnswer> = emptyList(),
    // Add lists for other potential elections (local, EU, etc.)
    // --- Callbacks ---
    onClearAndRestart: () -> Unit, // Action to clear votes and go back
    onConfirmVote: () -> Unit,    // Action to submit the vote
    onNavigateBack: () -> Unit     // Simple back navigation (e.g., to previous vote screen)
) {
    // --- State for potential loading/error during submission ---
    var isSubmitting by remember { mutableStateOf(false) }
    var submissionError by remember { mutableStateOf<String?>(null) }

    // --- Helper functions to find display names from IDs ---
    fun findPartyName(id: Int?): String? = parliamentParties.find { it.id == id }?.name
    fun findCandidateName(partyId: Int?, prefId: Int?): String? =
        parliamentCandidates.find { it.partyId == partyId && it.id == prefId }?.name
    fun findPresidentialOptionText(id: Int?): String? = presidentialOptions.find { it.id == id }?.partyName
    fun findReferendumAnswerText(id: Int?): String? = referendumAnswers.find { it.id == id }?.text
    fun findPresidentialNominees(id: Int?): String? = presidentialOptions.find { it.id == id }?.candidates


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "eVote",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding( start = 8.dp)
                    ) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        bottomBar = {
            // Buttons at the bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround, // Space out buttons
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clear Button
                OutlinedButton(
                    onClick = onClearAndRestart,
                    enabled = !isSubmitting, // Disable during submit
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("Изчисти") // "Clear"
                }
                // Vote Button
                Button(
                    onClick = {
                        isSubmitting = true // Show loading indicator
                        submissionError = null // Clear previous errors
                        // In a real app, call ViewModel here which handles DB interaction
                        // For now, just call the callback
                        onConfirmVote()
                        // isSubmitting = false would typically happen in the callback's result handling
                    },
                    enabled = !isSubmitting, // Disable during submit
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("ГЛАСУВАЙ", fontSize = 16.sp) // "VOTE"
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Display error message if submission failed
            item {
                if (submissionError != null) {
                    Text(
                        text = "Грешка при изпращане: $submissionError",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            // --- Parliament Selection Summary ---
            if (voteSummary.parliamentPartyId != null) {
                item {
                    SelectionSummaryCard(title = "Парламентарни избори") {
                        val partyName = findPartyName(voteSummary.parliamentPartyId) ?: "Неизвестен избор"
                        SelectionDetailRow("Избрана партия/коалиция:", partyName)

                        if (voteSummary.parliamentPreferenceId != null) {
                            val candidateName = findCandidateName(voteSummary.parliamentPartyId, voteSummary.parliamentPreferenceId)
                            SelectionDetailRow("Преференция:", "${voteSummary.parliamentPreferenceId ?: ""} ${candidateName ?: ""}".trim())
                        } else {
                            SelectionDetailRow("Преференция:", "-")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // --- Presidential Selection Summary ---
            if (voteSummary.presidentialOptionId != null) {
                item {
                    SelectionSummaryCard(title = "Избори за Президент и Вицепрезидент") { // Or dynamic title
                        val optionText = findPresidentialOptionText(voteSummary.presidentialOptionId) ?: "Неизвестен избор"
                        SelectionDetailRow("Избран вариант:", optionText)

                        // Optionally display nominees IF NOT "Support Nobody"
                        if(voteSummary.presidentialOptionId != SUPPORT_NOBODY_ID) {
                            val nominees = findPresidentialNominees(voteSummary.presidentialOptionId)
                            if (!nominees.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                SelectionDetailRow("Кандидати:", nominees.replace(";", "\n").replace(",", "\n").trim()) // Display nominees nicely
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // --- Referendum Selection Summary ---
            if (voteSummary.referendumAnswerId != null) {
                item {
                    SelectionSummaryCard(title = "Референдум") { // Or dynamic title
                        val answerText = findReferendumAnswerText(voteSummary.referendumAnswerId) ?: "Неизвестен избор"
                        SelectionDetailRow("Вашият отговор:", answerText)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // --- Add summaries for other election types here ---
            // e.g., Local, EU, etc., following the same pattern

        }
    }
}

// --- Helper Composables for structured display ---

@Composable
fun SelectionSummaryCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                fontSize = 16.sp
            )
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            // Inject the specific details content here
            content()
        }
    }
}

@Composable
fun SelectionDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.4f),
            fontSize = 16.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f),
            fontSize = 16.sp
        )
    }
    Spacer(modifier = Modifier.height(4.dp)) // Small space between detail rows
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 380)
@Composable
fun VotePreviewScreenPreview() {
    // Sample data for preview - mimic a combined election scenario
    val previewSummary = VoteSelectionSummary(
        parliamentPartyId = 8,
        parliamentPreferenceId = 108,
        presidentialOptionId = 1,
        referendumAnswerId = 1
    )

    // Sample data lists needed to display names
    val sampleParties = listOf(Party(8, "Коалиция „ДПС – Ново начало“"))
    val sampleCandidates = listOf(Candidate(108, "Канд. 8", 8))
    val samplePresidential = listOf(
        PresidentialPair(1, "ГЕРБ", "Иван Митрополски; Надежда Митева"),
        PresidentialPair(SUPPORT_NOBODY_ID, "Не подкрепям никого", "")
    )
    val sampleReferendumAnswers = listOf(ReferendumAnswer(1, "ДА"), ReferendumAnswer(2, "НЕ"))

    AppTheme {
        VotePreviewScreen(
            voteSummary = previewSummary,
            parliamentParties = sampleParties,
            parliamentCandidates = sampleCandidates,
            presidentialOptions = samplePresidential,
            referendumAnswers = sampleReferendumAnswers,
            onClearAndRestart = { println("Preview: Clear and Restart") },
            onConfirmVote = { println("Preview: Confirm Vote") },
            onNavigateBack = { println("Preview: Navigate Back") }
        )
    }
}