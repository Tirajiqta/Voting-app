package com.example.android.ui.theme.screens

import android.os.Build
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
fun VotingScreen(
    navController: NavController,
    electionId: Long,
    userId: Long // Pass the logged-in user's ID here
) {
    // --- ViewModel Setup ---
    val context = LocalContext.current
    // Repository setup (consider moving this to a central dependency injection solution)
    val appDatabase = remember { AppDatabase.getInstance(context.applicationContext) }
    val electionsRepository = remember {
        ElectionsRepository(
            electionDao = appDatabase.electionDao(),
            candidateDao = appDatabase.candidateDao(),
            partyDao = appDatabase.partyDao(),
            partyVoteDao = appDatabase.partyVoteDao(),
            voteDao = appDatabase.voteDao()
        )
    }
    // Create SavedStateHandle manually if not using Hilt/other DI that provides it
    val savedStateHandle = remember { SavedStateHandle(mapOf("electionId" to electionId)) }

    val viewModel: VotingViewModel = viewModel(
        factory = VotingViewModelFactory(electionsRepository, savedStateHandle)
    )

    // --- State Collection ---
    val uiState by viewModel.uiState.collectAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // --- Navigation Effect ---
    LaunchedEffect(uiState.voteSavedSuccessfully) {
        if (uiState.voteSavedSuccessfully) {
            // Navigate back or to a confirmation screen
            // Example: Pop back stack to the screen before voting
            scope.launch {
                snackbarHostState.showSnackbar("Гласът е запазен успешно!") // Optional feedback
            }
            navController.popBackStack()
            // Or navigate to a specific main screen route:
            // navController.navigate("main_screen_route") {
            //     popUpTo(navController.graph.startDestinationId) { inclusive = true }
            // }
        }
    }

    // --- Error Handling Effect ---
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            scope.launch {
                snackbarHostState.showSnackbar(errorMsg, duration = SnackbarDuration.Long)
            }
            // Optionally clear the error in the ViewModel after showing it
            // viewModel.clearError()
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(uiState.electionName.ifEmpty { "Гласуване" }) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        bottomBar = {
            // Button placed in the bottom bar area for consistent placement
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface, // Or another appropriate color
            ) {
                Spacer(Modifier.weight(1f)) // Push button to the end
                Button(
                    onClick = { showConfirmationDialog = true },
                    enabled = uiState.selectedPartyId != null && uiState.selectedCandidateId != null && !uiState.isLoading,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Следващ")
                }
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Apply padding from Scaffold
        ) {
            when {
                uiState.isLoading && uiState.parties.isEmpty() -> { // Show loading only initially
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                !uiState.isLoading && uiState.error != null && uiState.parties.isEmpty() -> { // Show error only if loading failed
                    Text(
                        text = uiState.error ?: "Възникна грешка",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                else -> {
                    // Main content: Party list and Preference grid
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp) // Padding around the row
                    ) {
                        // --- Party List (Left Side) ---
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f) // Takes half the width
                                .padding(end = 4.dp) // Space between columns
                                .border(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            items(uiState.parties, key = { it.id }) { party ->
                                PartyItem(
                                    party = party,
                                    isSelected = party.id == uiState.selectedPartyId,
                                    onPartyClick = { viewModel.selectParty(party.id) }
                                )
                                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }

                        // --- Preferences (Right Side) ---
                        Column(
                            modifier = Modifier
                                .weight(1f) // Takes the other half
                                .padding(start = 4.dp) // Space between columns
                                .border(1.dp, MaterialTheme.colorScheme.outline)
                                .padding(8.dp) // Inner padding for the preference area
                        ) {
                            Text(
                                "Предпочитание (Преференция)",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                textAlign = TextAlign.Center
                            )
                            if (uiState.selectedPartyId == null) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Моля, изберете партия", textAlign = TextAlign.Center)
                                }
                            } else if (uiState.candidatesForSelectedParty.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Няма преференции за тази партия",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = 50.dp), // Adjust minSize for circle size
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(uiState.candidatesForSelectedParty, key = { it.id }) { candidate ->
                                        PreferenceItem(
                                            candidate = candidate,
                                            isSelected = candidate.id == uiState.selectedCandidateId,
                                            onPreferenceClick = { viewModel.selectCandidate(candidate.id) }
                                        )
                                    }
                                }
                            }
                        }
                    } // End Row

                    // Show loading overlay when saving vote
                    if (uiState.isLoading && uiState.selectedPartyId != null) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center){
                            CircularProgressIndicator()
                        }
                    }
                } // End Else
            } // End When
        } // End Box

        // --- Confirmation Dialog ---
        if (showConfirmationDialog) {
            ConfirmationDialog(
                onConfirm = {
                    showConfirmationDialog = false
                    viewModel.saveVote(userId) // Pass the actual userId
                },
                onDismiss = { showConfirmationDialog = false }
            )
        }
    }
}

// --- Helper Composables ---

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
            .size(48.dp) // Size of the circle
            .clip(CircleShape)
            .background(if (isSelected) selectedColor else Color.Transparent)
            .border(
                width = 1.5.dp,
                color = if (isSelected) selectedColor else normalBorderColor, // Border same as background when selected
                shape = CircleShape
            )
            .clickable(onClick = onPreferenceClick),
        contentAlignment = Alignment.Center
    ) {
        // Draw 'X' if selected
        if (isSelected) {
            Text(
                text = "X", // Simple X, consider using an Icon if needed
                color = selectedContentColor,
                fontSize = 20.sp, // Adjust size as needed
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = (-1).dp) // Slight offset correction if needed
            )
            Text( // Overlay number slightly less prominently
                text = "${candidate.preferenceNumber}",
                color = selectedContentColor.copy(alpha = 0.7f), // Make number slightly transparent
                fontSize = 10.sp, // Smaller font for number under X
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp) // Position at bottom
            )
        } else {
            // Show only number if not selected
            Text(
                text = "${candidate.preferenceNumber}",
                color = LocalContentColor.current,
                fontSize = 14.sp, // Normal number size
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