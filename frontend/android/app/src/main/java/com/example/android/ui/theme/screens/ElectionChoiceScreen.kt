package com.example.android.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.AppTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip


object ElectionTypes {
    const val PARLIAMENT = "Парламент"
    const val GRAND_NATIONAL_ASSEMBLY = "Велико народно събрание (ВНС)"
    const val PRESIDENTIAL = "Президент"
    const val LOCAL = "Местни избори"
    const val REFERENDUM = "Референдум"
    const val EUROPEAN = "Европейски"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectionChoiceScreen(
    activeElections: List<String>, // Pass the list of currently active election names/IDs
    onNavigateToVote: (selectedElectionIds: List<String>) -> Unit,
) {
    val context = LocalContext.current

    // --- State for managing selections ---
    // Initialize map with all active elections set to false (not selected)
    val selectedStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            activeElections.forEach { election ->
                put(election, false) // Start with none selected
            }
        }
    }

    // Derived state to check if the "Proceed" button should be enabled
    val isProceedEnabled by remember {
        derivedStateOf { selectedStates.values.any { it } } // True if at least one is selected
    }

    // --- Side effect for single election ---
    LaunchedEffect(activeElections) {
        if (activeElections.size == 1) {
            // If only one election, navigate directly
            onNavigateToVote(activeElections) // Pass the single election in a list
        }
        // If size is 0 or > 1, the LaunchedEffect does nothing, UI will be shown.
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избор на гласуване") }, // "Election Selection"
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { paddingValues ->

        // Show selection UI only if there are multiple elections
        if (activeElections.size > 1) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply padding from Scaffold
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(16.dp), // Apply overall content padding
                verticalArrangement = Arrangement.Center, // Center the block vertically
                horizontalAlignment = Alignment.CenterHorizontally // Center items horizontally
            ) {
                Text(
                    "Моля, изберете в кои избори\nжелаете да участвате:", // "Please select which elections you wish to participate in:"
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Column(horizontalAlignment = Alignment.Start) { // Align checkbox rows to start
                    activeElections.forEach { electionName ->
                        CheckboxWithLabel(
                            label = electionName,
                            /*modifier = Modifier.background(
                                color = MaterialTheme.colorScheme.primaryContainer ,
                                shape = RoundedCornerShape(size = 12.dp),
                                ),*/
                            isChecked = selectedStates[electionName] ?: false,
                            onCheckedChange = { isChecked ->
                                selectedStates[electionName] = isChecked
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Space between checkboxes
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
                    enabled = isProceedEnabled, // Enable button only if something is selected
                    modifier = Modifier
                        .width(220.dp)
                        .padding(vertical = 8.dp
                        ),
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
        } else if (activeElections.isEmpty()) {
            // Optional: Handle case where there are no elections
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Няма активни избори в момента.") // "No active elections currently."
            }
        }
        // If activeElections.size == 1, the LaunchedEffect handles navigation,
        // so this Scaffold content area might briefly show before navigation or not at all.
        // You could add a loading indicator here if navigation takes time.
    }
}

@Composable
fun CheckboxWithLabel(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
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

@Preview(showBackground = true, name = "Selection Screen - Multiple Elections")
@Composable
fun ElectionSelectionScreenMultiplePreview() {
    AppTheme {
        ElectionChoiceScreen(
            activeElections = listOf(
                ElectionTypes.PARLIAMENT,
                ElectionTypes.EUROPEAN
            ),
            onNavigateToVote = { selectedIds -> println("Navigating to vote for: $selectedIds") },
        )
    }
}

@Preview(showBackground = true, name = "Selection Screen - Single Election")
@Composable
fun ElectionSelectionScreenSinglePreview() {
    AppTheme {
        // Note: In a real scenario, the LaunchedEffect would trigger navigation almost
        // immediately, so you might not see this UI state for long.
        ElectionChoiceScreen(
            activeElections = listOf(ElectionTypes.PRESIDENTIAL),
            onNavigateToVote = { selectedIds -> println("Navigating directly to vote for: $selectedIds") },
        )
    }
}

@Preview(showBackground = true, name = "Selection Screen - No Elections")
@Composable
fun ElectionSelectionScreenNonePreview() {
    AppTheme {
        ElectionChoiceScreen(
            activeElections = emptyList(),
            onNavigateToVote = { },
        )
    }
}