import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
// Import necessary classes from your project structure
import com.example.android.db.AppDatabase
import com.example.android.repository.ElectionsRepository
import com.example.android.ui.theme.viewmodels.ResultsViewModel
import com.example.android.ui.theme.viewmodels.ResultsViewModelFactory
import com.example.android.ui.theme.viewmodels.ResultsUiState

/**
 * Displays the election results, showing vote counts for parties and candidates.
 * This version displays the results as text lists within cards.
 *
 * @param electionId The ID of the election whose results are to be displayed. Passed via navigation.
 * @param onNavigateBack Callback function to navigate back to the previous screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    electionId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val appDatabase = remember { AppDatabase.getInstance(context.applicationContext) }

    val voteDao = appDatabase.voteDao()
    val electionDao = appDatabase.electionDao()
    val candidateDao = appDatabase.candidateDao()
    val partyDao = appDatabase.partyDao()
    val partyVoteDao = appDatabase.partyVoteDao()

    val electionsRepository = remember {
        ElectionsRepository(
            voteDao = voteDao,
            electionDao = electionDao,
            candidateDao = candidateDao,
            partyDao = partyDao,
            partyVoteDao = partyVoteDao
        )
    }

    val savedStateHandle = remember { SavedStateHandle(mapOf("electionId" to electionId)) }

    val viewModel: ResultsViewModel = viewModel(
        factory = ResultsViewModelFactory(electionsRepository, savedStateHandle)
    )

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.electionName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()

                uiState.error != null -> ErrorDisplay(uiState.error)

                !uiState.isLoading && uiState.partyResults.isEmpty() && uiState.candidateResults.isEmpty() -> {
                    Text(
                        "Няма налични резултати за този избор.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> ResultsContent(uiState)
            }
        }
    }
}

/**
 * Reusable composable to display error messages.
 */
@Composable
fun ErrorDisplay(errorMsg: String?) {
    Text(
        text = "Грешка: ${errorMsg ?: "Неизвестна грешка"}", // Provide default error message
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}

/**
 * Displays the main content area with lists of party and candidate results.
 *
 * @param uiState The current state containing the results data.
 */
@Composable
fun ResultsContent(uiState: ResultsUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp), // Padding around the entire list
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp) // Space between Party and Candidate sections
    ) {
        // --- Party Results Section ---
        item {
            Text(
                "Резултати по Партии",
                style = MaterialTheme.typography.headlineMedium // Title styling
            )
            Spacer(Modifier.height(8.dp)) // Space below title
            if (uiState.partyResults.isEmpty()) {
                Text("Няма данни за партии.")
            } else {
                // Use a Card for visual grouping
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Subtle shadow
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Background color for the card
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) { // Padding inside the card
                        // Iterate through party results and display each row
                        uiState.partyResults.forEachIndexed { index, result ->
                            PartyResultRow(
                                name = result.partyName ?: "Неизвестна ID: ${result.partyId}", // Display name or fallback ID
                                votes = result.voteCount
                            )
                            // Add divider between rows, but not after the last one
                            if (index < uiState.partyResults.lastIndex) {
                                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        } // End Party Results Item

        // --- Candidate Results Section ---
        item {
            Text(
                "Резултати по Кандидати (Преференции)",
                style = MaterialTheme.typography.headlineMedium // Title styling
            )
            Spacer(Modifier.height(8.dp)) // Space below title
            if (uiState.candidateResults.isEmpty()) {
                Text("Няма данни за преференции.")
            } else {
                // Use a Card for visual grouping
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) { // Padding inside the card
                        // Iterate through candidate results and display each row
                        uiState.candidateResults.forEachIndexed { index, result ->
                            CandidateResultRow(
                                name = result.candidateName ?: "Неизвестен ID: ${result.candidateId}", // Display name or fallback ID
                                votes = result.voteCount
                            )
                            // Add divider between rows, but not after the last one
                            if (index < uiState.candidateResults.lastIndex) {
                                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        } // End Candidate Results Item
    } // End LazyColumn
}

/**
 * Helper composable for displaying a single row in the party results list.
 */
@Composable
fun PartyResultRow(name: String, votes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp), // Padding for each row content
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Pushes votes to the end
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f, fill = false), // Allow name to wrap if needed, don't force fill
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2 // Limit name lines if very long
        )
        Spacer(modifier = Modifier.width(16.dp)) // Ensure space even if name is short
        Text(
            text = votes.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold // Make vote count stand out
        )
    }
}

/**
 * Helper composable for displaying a single row in the candidate results list.
 */
@Composable
fun CandidateResultRow(name: String, votes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp), // Padding for each row content
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Pushes votes to the end
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f, fill = false), // Allow name to wrap
            style = MaterialTheme.typography.bodyMedium, // Slightly smaller text for candidates
            maxLines = 2
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = votes.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}