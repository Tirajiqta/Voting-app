package com.example.android.ui.theme.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.android.db.AppDatabase
import com.example.android.repository.ElectionsRepository
import com.example.android.ui.theme.viewmodels.CandidateDisplayItem
import com.example.android.ui.theme.viewmodels.PartyDisplayItem
import com.example.android.ui.theme.viewmodels.VotingViewModel
import com.example.android.ui.theme.viewmodels.VotingViewModelFactory
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotingScreen( // This is the wrapper/container composable
    navController: NavController,
    electionId: Long,
    userId: Long
) {
    // --- ViewModel Setup (Same as before) ---
    val context = LocalContext.current
    val appDatabase = remember { AppDatabase.getInstance(context.applicationContext) }
    val electionsRepository = remember {
        ElectionsRepository( /* ... DAOs ... */
            electionDao = appDatabase.electionDao(),
            candidateDao = appDatabase.candidateDao(),
            partyDao = appDatabase.partyDao(),
            partyVoteDao = appDatabase.partyVoteDao(),
            voteDao = appDatabase.voteDao()
        )
    }
    val savedStateHandle = remember { SavedStateHandle(mapOf("electionId" to electionId)) }
    val viewModel: VotingViewModel = viewModel(
        factory = VotingViewModelFactory(electionsRepository, savedStateHandle)
    )

    // --- State Collection ---
    val uiState by viewModel.uiState.collectAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    // Store the IDs to be saved when the dialog is confirmed
    var partyIdToSave by remember { mutableStateOf<Int?>(null) }
    var preferenceIdToSave by remember { mutableStateOf<Long?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // --- Effects for Navigation and Snackbar (Same as before) ---
    LaunchedEffect(uiState.voteSavedSuccessfully) {
        if (uiState.voteSavedSuccessfully) {
            // ONLY show snackbar here, DO NOT navigate or pop back stack
            scope.launch {
                snackbarHostState.showSnackbar("Гласът е запазен успешно!")
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            scope.launch {
                snackbarHostState.showSnackbar(errorMsg, duration = SnackbarDuration.Long)
            }
            viewModel.clearError() // Clear error after showing
        }
    }

    // --- UI ---
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Initial Loading State
                uiState.isLoading && uiState.parties.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // Initial Error State
                !uiState.isLoading && uiState.error != null && uiState.parties.isEmpty() -> {
                    Text(
                        text = uiState.error ?: "Възникна грешка",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                // Data Loaded State
                else -> {
                    ParliamentVoteScreen(
                        // Pass data from ViewModel State
                        electionTitle = uiState.electionTitle,
                        electionDate = uiState.electionDate,
                        parties = uiState.parties,
                        candidates = uiState.candidates,
                        onNavigateBack = { navController.popBackStack() }, // Simple back navigation
                        onReviewVote = { selectedPartyId, selectedPreferenceId ->
                            // Store the selected IDs and show the dialog
                            partyIdToSave = selectedPartyId
                            preferenceIdToSave = selectedPreferenceId
                            showConfirmationDialog = true
                        }
                    )

                    // Overlay loading indicator when saving vote
                    if (uiState.isLoading && !uiState.parties.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }

        //Confirmation Dialog (Remains the same, triggered differently)
        if (showConfirmationDialog) {
            ConfirmationDialog(
                onConfirm = {
                    showConfirmationDialog = false
                    // Use the stored IDs to save the vote
                    partyIdToSave?.let { pId -> // pId is the Party's Int ID from UI
                        val selectedPreferenceNumber = preferenceIdToSave // The Int preference number

                        var actualCandidatePrimaryKey: Long? = null
                        if (selectedPreferenceNumber != null) {
                            val foundCandidate = uiState.candidates.firstOrNull { candidate ->
                                candidate.partyId == pId && candidate.preferenceNumber.toLong() == selectedPreferenceNumber
                            }

                            if (foundCandidate != null) {
                                actualCandidatePrimaryKey =
                                    (foundCandidate.id ?: 1) as Long? // Get the actual Long PK
                                Log.d("VOTE_SAVE_UI_MAP", "UI Mapping: Found PK $actualCandidatePrimaryKey for Pref $selectedPreferenceNumber in Party $pId")
                            } else {
                                Log.e("VOTE_SAVE_UI_MAP", "UI Mapping ERROR: Candidate not found for Pref $selectedPreferenceNumber in Party $pId")
                                scope.launch { snackbarHostState.showSnackbar("Грешка: Избраната преференция е невалидна за тази партия.") }
                                partyIdToSave = null
                                preferenceIdToSave = null
                                return@let
                            }
                        } else {
                            Log.d("VOTE_SAVE_UI_MAP", "UI Mapping: No preference selected, candidate PK remains null.")
                            actualCandidatePrimaryKey = null
                        }
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }

                    }
                    partyIdToSave = null
                    preferenceIdToSave = null
                },
                onDismiss = {
                    showConfirmationDialog = false
                    partyIdToSave = null
                    preferenceIdToSave = null
                }
            )
        }
    }
}




@Composable
fun PartyItem(
    party: PartyDisplayItem,
    isSelected: Boolean,
    onPartyClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) MaterialTheme.colorScheme.inverseSurface else Color.Transparent)
            .clickable(onClick = onPartyClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${party.number}",
            modifier = Modifier
                .width(30.dp) // Fixed width for number box
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .padding(vertical = 4.dp),
            textAlign = TextAlign.Center,
            color = if (isSelected) MaterialTheme.colorScheme.inverseOnSurface else LocalContentColor.current,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = party.name,
            color = if (isSelected) MaterialTheme.colorScheme.inverseOnSurface else LocalContentColor.current,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f) // Take remaining space
        )
    }
}

@Composable
fun PreferenceItem(
    candidate: CandidateDisplayItem,
    isSelected: Boolean,
    onPreferenceClick: () -> Unit
) {
    val selectedColor = MaterialTheme.colorScheme.inverseSurface
    val selectedContentColor = MaterialTheme.colorScheme.inverseOnSurface
    val normalBorderColor = MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isSelected) selectedColor else Color.Transparent)
            .border(
                width = 1.5.dp,
                color = if (isSelected) selectedColor else normalBorderColor,
                shape = CircleShape
            )
            .clickable(onClick = onPreferenceClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Text(
                text = "X",
                color = selectedContentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = (-1).dp)
            )
            Text(
                text = "${candidate.preferenceNumber}",
                color = selectedContentColor.copy(alpha = 0.7f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp)
            )
        } else {
            Text(
                text = "${candidate.preferenceNumber}",
                color = LocalContentColor.current,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Потвърждение") },
        text = { Text("Сигурни ли сте, че искате да запазите този глас? Това действие не може да бъде отменено.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Да")

            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Не")
            }
        }
    )
}