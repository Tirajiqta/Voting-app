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
import com.example.android.db.AppDatabase
import com.example.android.repository.ElectionsRepository
import com.example.android.ui.theme.viewmodels.ElectionChoiceUiState
import com.example.android.ui.theme.viewmodels.ElectionChoiceViewModel
import com.example.android.ui.theme.viewmodels.ElectionChoiceViewModelFactory
import com.example.android.ui.theme.viewmodels.ElectionDisplayItem
import com.example.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectionChoiceScreen(
    onNavigateToVote: (selectedElectionIds: List<Long>) -> Unit,
) {
    val context = LocalContext.current
    val appDatabase = remember { AppDatabase.getInstance(context.applicationContext) }

// Get DAOs from the Room database instance
    val electionDao = appDatabase.electionDao()
    val candidateDao = appDatabase.candidateDao()
    val partyDao = appDatabase.partyDao()
    val partyVoteDao = appDatabase.partyVoteDao()
    val voteDao = appDatabase.voteDao()
    val electionsRepository = remember { ElectionsRepository(
        electionDao = electionDao,
        candidateDao = candidateDao,
        partyDao = partyDao,
        partyVoteDao = partyVoteDao,
        voteDao = voteDao
    )}

    // Create the ViewModel Factory instance
    val electionChoiceViewModelFactory = remember {
        ElectionChoiceViewModelFactory(electionsRepository) // Pass the repository instance
    }

    val viewModel: ElectionChoiceViewModel = viewModel(
        factory = electionChoiceViewModelFactory // <--- PROVIDE THE FACTORY HERE
    )

    val uiState by viewModel.uiState.collectAsState()
    val selectedStates = remember { mutableStateMapOf<Long, Boolean>() }

    LaunchedEffect(uiState.elections) {
        selectedStates.clear()
        uiState.elections.forEach { election ->
            selectedStates[election.id ?: -1L] = false
        }
    }

    val isProceedEnabled by remember {
        derivedStateOf { selectedStates.values.any { it } }
    }

    LaunchedEffect(uiState.elections, uiState.isLoading) {
        if (!uiState.isLoading && uiState.elections.size == 1) {
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
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Loading State
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                // Error State
                uiState.error != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Грешка: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(onClick = { viewModel.loadElections() }) {
                            Text("Опитай пак")
                        }
                    }
                }
                // Empty State
                uiState.elections.isEmpty() -> {
                    Text("Няма активни избори в момента.")
                }
                uiState.elections.size > 1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Моля, изберете в кои избори\nжелаете да участвате:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Column(horizontalAlignment = Alignment.Start) {
                            uiState.elections.forEach { election ->
                                CheckboxWithLabel(
                                    label = election.name,
                                    isChecked = selectedStates[election.id] ?: false,
                                    onCheckedChange = { isChecked ->
                                        selectedStates[election.id] = isChecked
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                val selectedIds = selectedStates.filter { it.value }.keys.toList()
                                if (selectedIds.isNotEmpty()) {
                                    onNavigateToVote(selectedIds)
                                } else {
                                    Toast.makeText(context, "Моля, изберете за какво ще гласувате.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = isProceedEnabled,
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

                uiState.elections.size == 1 -> {

                    Spacer(Modifier.fillMaxSize()) // Keep the space occupied
                }
            }
        }
    }
}

@Composable
fun CheckboxWithLabel(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 16.sp)
    }
}


@Preview(showBackground = true, name = "Selection Screen - Loading")
@Composable
fun ElectionSelectionScreenLoadingPreview() {
    AppTheme {
        val loadingState = ElectionChoiceUiState(isLoading = true)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewableElectionChoiceScreen(
    uiState: ElectionChoiceUiState,
    onNavigateToVote: (List<Long>) -> Unit = {} // Dummy action
) {
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
                uiState.elections.size == 1 -> {
                    Text("Пренасочване към избори: ${uiState.elections.first().name}") // Simulate auto-nav message
                }
            }
        }
    }
}