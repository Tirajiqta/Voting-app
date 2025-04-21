import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.dao.election.CandidateDao
import com.example.android.dao.election.ElectionDao
import com.example.android.dao.election.PartyDao
import com.example.android.dao.election.PartyVoteDao
import com.example.android.dao.election.VoteDao
import com.example.android.db.Database
import com.example.android.entity.election.CandidateEntity
import com.example.android.entity.election.ElectionEntity
import com.example.android.entity.election.PartyEntity
import com.example.android.entity.election.PartyVoteEntity
import com.example.android.entity.election.VoteEntity
import com.example.android.repository.ElectionsRepository
import com.example.android.ui.theme.viewmodels.ElectionChoiceUiState
import com.example.android.ui.theme.viewmodels.ElectionChoiceViewModel
import com.example.android.ui.theme.viewmodels.ElectionChoiceViewModelFactory
import com.example.android.ui.theme.viewmodels.ElectionDisplayItem
import com.example.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectionChoiceScreen(
    // Inject ViewModel
//    viewModel: ElectionChoiceViewModel = viewModel(),
    // Navigation callback expects List<Long> (election IDs)
    onNavigateToVote: (selectedElectionIds: List<Long>) -> Unit,
) {
    val context = LocalContext.current
    val appDatabase = remember { Database.getInstance(context.applicationContext) } // Adjust getInstance if needed
    val writableDb = remember { appDatabase.writableDatabase }

    val electionDao = remember { ElectionDao(writableDb) }
    val candidateDao = remember { CandidateDao(writableDb) }
    val partyDao = remember { PartyDao(writableDb) }
    val partyVoteDao = remember { PartyVoteDao(writableDb) }
    val voteDao = remember { VoteDao(writableDb) }
    // 3. Create the ElectionsRepository instance using the DAOs
    val electionsRepository = remember { ElectionsRepository(
        electionDao = electionDao,
        candidateDao = candidateDao,
        partyDao = partyDao,
        partyVoteDao = partyVoteDao,
        voteDao = voteDao
    )}

    // 4. Create the ViewModel Factory instance
    val electionChoiceViewModelFactory = remember {
        ElectionChoiceViewModelFactory(electionsRepository) // Pass the repository instance
    }

    // --- Get ViewModel using the Factory ---
    val viewModel: ElectionChoiceViewModel = viewModel(
        factory = electionChoiceViewModelFactory // <--- PROVIDE THE FACTORY HERE
    )

    // --- The rest of your Composable code remains the same ---
    val uiState by viewModel.uiState.collectAsState()
    val selectedStates = remember { mutableStateMapOf<Long, Boolean>() }

    LaunchedEffect(uiState.elections) {
        selectedStates.clear()
        uiState.elections.forEach { election ->
            // Use a safe default if ID can be null, though IDs should usually be non-null
            selectedStates[election.id ?: -1L] = false
        }
    }

    val isProceedEnabled by remember {
        derivedStateOf { selectedStates.values.any { it } }
    }

    LaunchedEffect(uiState.elections, uiState.isLoading) {
        if (!uiState.isLoading && uiState.elections.size == 1) {
            // Use a safe default if ID can be null
            val singleElectionId = uiState.elections.first().id ?: -1L
            if (singleElectionId != -1L) { // Only navigate if ID is valid
                onNavigateToVote(listOf(singleElectionId))
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избор на гласуване") },
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
                .padding(paddingValues), // Apply padding from Scaffold
            contentAlignment = Alignment.Center // Center content (loading/error/list)
        ) {
            when {
                // --- Loading State ---
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                // --- Error State ---
                uiState.error != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Грешка: ${uiState.error}", // "Error: ..."
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(onClick = { viewModel.loadElections() }) { // Retry button
                            Text("Опитай пак") // "Try Again"
                        }
                    }
                }
                // --- Empty State ---
                uiState.elections.isEmpty() -> {
                    Text("Няма активни избори в момента.") // "No active elections currently."
                }
                // --- Success State (Multiple Elections) ---
                // Show selection UI only if loading is done, no error, and more than one election
                uiState.elections.size > 1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .padding(16.dp), // Apply overall content padding
                        verticalArrangement = Arrangement.Center, // Center the block vertically
                        horizontalAlignment = Alignment.CenterHorizontally // Center items horizontally
                    ) {
                        Text(
                            "Моля, изберете в кои избори\nжелаете да участвате:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Column(horizontalAlignment = Alignment.Start) { // Align checkbox rows to start
                            // Iterate through ElectionDisplayItem from uiState
                            uiState.elections.forEach { election ->
                                CheckboxWithLabel(
                                    label = election.name, // Use name from display item
                                    isChecked = selectedStates[election.id] ?: false,
                                    onCheckedChange = { isChecked ->
                                        selectedStates[election.id] = isChecked
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp)) // Space between checkboxes
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                // Filter the map keys where the value is true to get selected IDs
                                val selectedIds = selectedStates.filter { it.value }.keys.toList()
                                if (selectedIds.isNotEmpty()) {
                                    onNavigateToVote(selectedIds) // Pass the list of Long IDs
                                } else {
                                    // This case should ideally be prevented by the button's enabled state
                                    Toast.makeText(context, "Моля, изберете за какво ще гласувате.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = isProceedEnabled, // Use derived state
                            modifier = Modifier
                                .width(220.dp)
                                .padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                "ПРОДЪЛЖИ",
                                fontSize = 18.sp,
                                letterSpacing = 3.sp
                            )
                        }
                    }
                }
                // --- Success State (Single Election) ---
                // If size is 1, the LaunchedEffect handles navigation.
                // Optionally show a brief message or keep the loading indicator
                // until navigation occurs if it's not instant.
                // A simple Spacer can prevent content shift.
                uiState.elections.size == 1 -> {
                    // Handled by LaunchedEffect, display nothing or a placeholder/spinner
                    // CircularProgressIndicator() // Or Text("Preparing your ballot...")
                    Spacer(Modifier.fillMaxSize()) // Keep the space occupied
                }
            }
        }
    }
}

// CheckboxWithLabel remains the same as you provided
@Composable
fun CheckboxWithLabel(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier
            .fillMaxWidth() // Allow row to take width for better alignment if needed
            .clickable { onCheckedChange(!isChecked) } // Make row clickable
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange // Allow direct checkbox click too
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 16.sp)
    }
}

// --- Previews ---
// Previews need adjustment as the screen now relies on ViewModel state.
// You might need a fake ViewModel or pass state directly for previews.

@Preview(showBackground = true, name = "Selection Screen - Loading")
@Composable
fun ElectionSelectionScreenLoadingPreview() {
    AppTheme {
        // Simulate loading state for preview
        val loadingState = ElectionChoiceUiState(isLoading = true)
        // Provide a dummy ViewModel or directly use state in preview composable
        // For simplicity, let's create a temporary composable for preview
        PreviewableElectionChoiceScreen(uiState = loadingState)
    }
}

@Preview(showBackground = true, name = "Selection Screen - Multiple Elections")
@Composable
fun ElectionSelectionScreenMultiplePreview() {
    AppTheme {
        val multipleElectionsState = ElectionChoiceUiState(
            isLoading = false,
            elections = listOf(
                ElectionDisplayItem(1L, "Парламент"),
                ElectionDisplayItem(2L, "Европейски")
            )
        )
        PreviewableElectionChoiceScreen(uiState = multipleElectionsState)
    }
}

@Preview(showBackground = true, name = "Selection Screen - Error")
@Composable
fun ElectionSelectionScreenErrorPreview() {
    AppTheme {
        val errorState = ElectionChoiceUiState(
            isLoading = false,
            error = "Network connection failed"
        )
        PreviewableElectionChoiceScreen(uiState = errorState)
    }
}

@Preview(showBackground = true, name = "Selection Screen - No Elections")
@Composable
fun ElectionSelectionScreenNonePreview() {
    AppTheme {
        val emptyState = ElectionChoiceUiState(isLoading = false, elections = emptyList())
        PreviewableElectionChoiceScreen(uiState = emptyState)
    }
}


// Helper Composable for Previewing with State (avoids needing ViewModel instance)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewableElectionChoiceScreen(
    uiState: ElectionChoiceUiState,
    onNavigateToVote: (List<Long>) -> Unit = {} // Dummy action
) {
    // Re-implement the core UI logic using the provided uiState
    val context = LocalContext.current
    val selectedStates = remember { mutableStateMapOf<Long, Boolean>() }
    LaunchedEffect (uiState.elections) {
        selectedStates.clear()
        uiState.elections.forEach { selectedStates[it.id] = false }
    }
    val isProceedEnabled by remember { derivedStateOf { selectedStates.values.any { it } } }

    Scaffold (
        topBar = { TopAppBar(title = { Text("Избор на гласуване") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> Text("Грешка: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                uiState.elections.isEmpty() -> Text("Няма активни избори.")
                uiState.elections.size > 1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Моля, изберете...", textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 24.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            uiState.elections.forEach { election ->
                                CheckboxWithLabel(
                                    label = election.name,
                                    isChecked = selectedStates[election.id] ?: false,
                                    onCheckedChange = { isChecked -> selectedStates[election.id] = isChecked }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                val selectedIds = selectedStates.filter { it.value }.keys.toList()
                                if (selectedIds.isNotEmpty()) onNavigateToVote(selectedIds)
                            },
                            enabled = isProceedEnabled,
                            modifier = Modifier.width(220.dp)
                        ) {
                            Text("ПРОДЪЛЖИ")
                        }
                    }
                }
                // Handle single election case if needed for preview visualization
                uiState.elections.size == 1 -> {
                    Text("Пренасочване към избори: ${uiState.elections.first().name}") // Simulate auto-nav message
                }
            }
        }
    }
}