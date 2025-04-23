package com.example.android.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.dummymodel.Candidate // Import dummy data classes
import com.example.android.dummymodel.Party    // Import dummy data classes
import com.example.compose.AppTheme // Import your theme

//Screen for EU parliament, BG parliament, local council

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ParliamentVoteScreen(
    electionTitle: String = "Избори за народно събрание",
    electionDate: String = "27.10.2024",
    parties: List<Party>,
    candidates: List<Candidate>,
    onNavigateBack: () -> Unit,
    onReviewVote: (selectedPartyId: Int, selectedPreferenceId: Long?) -> Unit
) {
    // --- State ---
    var selectedPartyId by remember { mutableStateOf<Int?>(null) }
    var selectedPreferenceId by remember { mutableStateOf<Long?>(null) }

    val currentPartyCandidates by remember(selectedPartyId) {
        derivedStateOf {
            if (selectedPartyId == null) emptyList() else candidates.filter { it.partyId == selectedPartyId }
        }
    }

    // Reset preference when party changes
    LaunchedEffect(selectedPartyId) {
        selectedPreferenceId = null
    }

    // --- UI ---
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
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        selectedPartyId?.let { partyId ->
                            onReviewVote(partyId, selectedPreferenceId)
                        }
                    },
                    enabled = selectedPartyId != null,
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(
                        "ПРЕГЛЕД",
                        letterSpacing = 4.sp,
                        fontSize = 20.sp)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // --- Header ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = electionTitle,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = electionDate,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider() // Separator line
                }
            }

            items(parties, key = { it.id }) { party ->
                val isSelected = party.id == selectedPartyId
                PartyItem(
                    party = party,
                    isSelected = isSelected,
                    onSelected = {
                        selectedPartyId = party.id
                    }
                )

                if (isSelected) {
                    PreferenceSelection(
                        candidates = currentPartyCandidates,
                        selectedPreferenceId = selectedPreferenceId,
                        onPreferenceSelected = { candidateId ->
                            selectedPreferenceId = candidateId
                        }
                    )
                }
                //
            }
        }
    }
}

@Composable
fun PartyItem(
    party: Party,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val shape = RoundedCornerShape(4.dp)

    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(backgroundColor, shape)
            .clickable { onSelected() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ballot Number Box
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {

                Text(
                    text = "X",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = party.id.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp

                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = party.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PreferenceSelection(
    candidates: List<Candidate>,
    selectedPreferenceId: Long?,
    onPreferenceSelected: (candidateId: Long?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        Text(
            text = "Предпочитание (Преференция)",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            candidates.forEach { candidate ->
                PreferenceCircle(
                    candidateId = candidate.id.toInt(),
                    isSelected = candidate.id == selectedPreferenceId,
                    onSelected = {
                        val newSelection = if (candidate.id == selectedPreferenceId) null else candidate.id
                        onPreferenceSelected(newSelection)
                    }

                )
            }
        }

    }
}

@Composable
fun PreferenceCircle(
    candidateId: Int,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent

    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .border(1.5.dp, borderColor, CircleShape)
            .background(backgroundColor, CircleShape)
            .clickable { onSelected() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = candidateId.toString(),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = if (isSelected) MaterialTheme.colorScheme.primary else LocalContentColor.current
        )
        if (isSelected) {
            Text(
                text = "X",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = (-1).dp)
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 380)
@Composable
fun ParliamentVoteScreenPreview() {
    // Sample Data for Preview
    val sampleParties = List(7) { index ->
        Party(id = index + 1, name = "Партия / Коалиция ${index + 1}")
    }.plus(Party(id = 8, name = "Не подкрепям никого"))

    val sampleCandidates = listOf(
        // Candidates for Party 8
        Candidate(
            id = 1, name = "Канд. 1", partyId = 1,
            preferenceNumber = 101
        ),
        Candidate(
            id = 2, name = "Канд. 2", partyId = 1,
            preferenceNumber = 102
        ),
        Candidate(
            id = 3, name = "Канд. 3", partyId = 2,
            preferenceNumber = 101
        ),
        Candidate(
            id = 4, name = "Канд. 4", partyId = 2,
            preferenceNumber = 102
        ),
        Candidate(
            id = 5, name = "Канд. 5", partyId = 3,
            preferenceNumber = 101
        ),
        Candidate(
            id = 6, name = "Канд. 6", partyId = 3,
            preferenceNumber = 102
        ),
        Candidate(
            id = 7, name = "Канд. 7", partyId = 4,
            preferenceNumber = 101
        ),
        Candidate(
            id = 8, name = "Канд. 8", partyId = 4,
            preferenceNumber = 102
        ),
        Candidate(
            id = 9, name = "Канд. 9", partyId = 5,
            preferenceNumber = 101
        ),
        Candidate(
            id = 10, name = "Канд. 10", partyId = 5,
            preferenceNumber = 102
        ),
        Candidate(
            id = 11, name = "Канд. 105", partyId = 5,
            preferenceNumber = 103
        ),
        Candidate(
            id = 12, name = "Канд. 11", partyId = 6,
            preferenceNumber = 101
        ),
        Candidate(
            id = 13, name = "Канд. 12", partyId = 6,
            preferenceNumber = 102
        ),
        // Add candidates for other parties if needed for testing
        Candidate(
            id = 14, name = "Друг 1", partyId = 7,
            preferenceNumber = 101
        ),
        Candidate(
            id = 15, name = "Друг 2", partyId = 7,
            preferenceNumber = 102
        ),
    )

    AppTheme {
        ParliamentVoteScreen(
            parties = sampleParties,
            candidates = sampleCandidates,
            onNavigateBack = {},
            onReviewVote = { partyId, prefId ->
                println("Review Vote -> Party: $partyId, Preference: $prefId")
            }
        )
    }
}