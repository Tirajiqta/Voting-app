package com.example.android.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.android.dummymodel.PresidentialPair // Import your data class
import com.example.android.dummymodel.SUPPORT_NOBODY_ID // Import the constant
import com.example.compose.AppTheme // Import your theme

// screen for President, mayor of town, mayor of district, mayor of municipality

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresidentVoteScreen(
    electionTitle: String = "Избори за Президент и Вицепрезидент", // Example Title
    electionDate: String = "27.10.2024",
    options: List<PresidentialPair>, // List including "Support Nobody"
    onNavigateBack: () -> Unit,
    onReviewVote: (selectedOptionId: Int) -> Unit // Pass only the selected ID
) {
    // --- State ---
    var selectedOptionId by remember { mutableStateOf<Int?>(null) }

    // --- UI ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
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
                        selectedOptionId?.let { optionId ->
                            onReviewVote(optionId)
                        }
                    },
                    enabled = selectedOptionId != null, // Enabled only when an option is selected
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(
                        "ПРЕГЛЕД",
                        letterSpacing = 4.sp,
                        fontSize = 20.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(horizontal = 16.dp), // Horizontal padding for list content
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
                    Divider()
                }
            }

            // --- Option List ---
            items(options, key = { it.id }) { option ->
                val isSelected = option.id == selectedOptionId
                PresidentialItem(
                    option = option,
                    isSelected = isSelected,
                    onSelected = {
                        selectedOptionId = option.id
                    }
                )

                // --- Candidate Names Display (Conditional) ---
                // Show only if this option is selected AND it's not the "Support Nobody" option
                if (isSelected && option.id != SUPPORT_NOBODY_ID) {
                    CandidateDisplay(
                        nomineesString = option.candidates
                    )
                }

                // Add a divider unless it's the support nobody option which might be visually distinct
                if (option.id != SUPPORT_NOBODY_ID) {
                    //Divider()
                } else {
                    Spacer(modifier = Modifier.height(8.dp)) // Add some space after support nobody
                }
            }
        }
    }
}

// --- Composable for a single Presidential Option Item ---
@Composable
fun PresidentialItem(
    option: PresidentialPair,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val shape = RoundedCornerShape(4.dp)

    // Special handling for the "Support Nobody" checkbox look
    val isSupportNobody = option.id == SUPPORT_NOBODY_ID

    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(
                1.dp,
                // Don't draw border around the checkbox item row if it's SupportNobody
                if (isSupportNobody) Color.Transparent else borderColor,
                shape
            )
            .background(
                if (isSupportNobody) Color.Transparent else backgroundColor, // No background for checkbox item
                shape
            )
            .clickable { onSelected() }
            .padding(
                vertical = if (isSupportNobody) 4.dp else 10.dp, // Less vertical padding for checkbox
                horizontal = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ballot Number Box OR Checkbox for "Support Nobody"
        Box(
            modifier = Modifier
                .size(32.dp)
                // Apply standard border unless it's the support nobody option
                .then(
                    if (!isSupportNobody) {
                        Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                    } else {
                        Modifier // No border needed, checkbox draws its own box
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSupportNobody) {
                // Display a Checkbox visual (doesn't need state, reflects parent selection)
                Checkbox(checked = isSelected, onCheckedChange = { onSelected() })
            } else if (isSelected) {
                // Display "X" when selected (and not support nobody)
                Text(
                    text = "X",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            } else {
                // Display the option ID number when not selected
                Text(
                    text = option.id.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        //Spacer(modifier = Modifier.width(12.dp))
        // Option Name (Party or "Не подкрепям никого")
        Text(
            text = option.partyName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
                .padding(start = 12.dp)
        )
    }
}

// --- Composable for displaying candidate names ---
@Composable
fun CandidateDisplay(nomineesString: String) {
    // Split the string into individual names
    // Handle potential variations in separator (;, ,, multiple spaces)
    val nomineeNames = remember(nomineesString) {
        nomineesString.split(Regex("[;,] *")) // Split by semicolon or comma, trimming spaces
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp) // Indent slightly
    ) {
        if (nomineeNames.isNotEmpty()) {
            Text(
                text = "Кандидати", // "Candidates"
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            nomineeNames.forEach { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        // Else: Don't show anything if the nominee string was empty or invalid
    }
}


// --- Previews ---
@Preview(showBackground = true, widthDp = 380)
@Composable
fun PresidentVoteScreenPreview() {
    // Sample Data for Preview
    val sampleOptions = List(7) { index ->
        PresidentialPair(
            id = index + 1,
            partyName = "Партия / Коалиция ${index + 1}",
            candidates = "Кандидат ${index + 1}А; Кандидат ${index + 1}Б"
        )
    }.plus(
        // Add the "Support Nobody" option
        PresidentialPair(
            id = SUPPORT_NOBODY_ID,
            partyName = "Не подкрепям никого",
            candidates = "" // No nominees for this option
        )
    )

    AppTheme {
        PresidentVoteScreen(
            options = sampleOptions,
            onNavigateBack = {},
            onReviewVote = { optionId ->
                println("Review Vote -> Option ID: $optionId")
            }
        )
    }
}