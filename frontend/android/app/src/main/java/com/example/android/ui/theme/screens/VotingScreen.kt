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
    userId: Long
) {
    val context = LocalContext.current
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
    val savedStateHandle = remember { SavedStateHandle(mapOf("electionId" to electionId)) }

    val viewModel: VotingViewModel = viewModel(
        factory = VotingViewModelFactory(electionsRepository, savedStateHandle)
    )

    val uiState by viewModel.uiState.collectAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.voteSavedSuccessfully) {
        if (uiState.voteSavedSuccessfully) {
            scope.launch {
                snackbarHostState.showSnackbar("Гласът е запазен успешно!") // Optional feedback
            }
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            scope.launch {
                snackbarHostState.showSnackbar(errorMsg, duration = SnackbarDuration.Long)
            }
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
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                Spacer(Modifier.weight(1f))
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
            .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && uiState.parties.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

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

                else -> {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
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

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline)
                                .padding(8.dp)
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
                                    columns = GridCells.Adaptive(minSize = 50.dp),
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
                    }
                    if (uiState.isLoading && uiState.selectedPartyId != null) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center){
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }

        if (showConfirmationDialog) {
            ConfirmationDialog(
                onConfirm = {
                    showConfirmationDialog = false
                    viewModel.saveVote(userId)
                },
                onDismiss = { showConfirmationDialog = false }
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
                .width(30.dp)
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
            modifier = Modifier.weight(1f)
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