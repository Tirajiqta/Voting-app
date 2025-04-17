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
import com.example.android.dummymodel.ReferendumAnswer // Import your data class
import com.example.compose.AppTheme // Import your theme

// Referendum screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferendumScreen(
    referendumQuestion: String, // The main question being asked
    answers: List<ReferendumAnswer>, // The possible answers (e.g., Yes, No)
    onNavigateBack: () -> Unit,
    onReviewVote: (selectedAnswerId: Int) -> Unit // Pass the ID of the selected answer
) {
    // --- State ---
    var selectedAnswerId by remember { mutableStateOf<Int?>(null) }

    // --- UI ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Референдум") }, // Or pass title as parameter
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
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
                        selectedAnswerId?.let { answerId ->
                            onReviewVote(answerId)
                        }
                    },
                    enabled = selectedAnswerId != null, // Enabled only when an answer is selected
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("ПРЕГЛЕД", fontSize = 16.sp) // "REVIEW"
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(horizontal = 16.dp), // Horizontal padding for list content
            contentPadding = PaddingValues(vertical = 16.dp) // Padding top/bottom of list
        ) {
            // --- Referendum Question ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp), // Space below question
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = referendumQuestion,
                        style = MaterialTheme.typography.headlineSmall, // Style for the question
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider() // Separator line
                }
            }

            // --- Answer List ---
            items(answers, key = { it.id }) { answer ->
                val isSelected = answer.id == selectedAnswerId
                ReferendumAnswerItem(
                    answer = answer,
                    isSelected = isSelected,
                    onSelected = {
                        selectedAnswerId = answer.id
                    }
                )
                // Add spacing between answer items
                Spacer(modifier = Modifier.height(8.dp))
                // Optional: Add Divider if needed
                // Divider()
            }
        }
    }
}

// --- Composable for a single Answer Item ---
@Composable
fun ReferendumAnswerItem(
    answer: ReferendumAnswer,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    // Appearance when selected
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val shape = RoundedCornerShape(8.dp) // Slightly more rounded? Adjust as needed.

    // Box appearance (where the 'X' goes)
    val selectionBoxShape = RoundedCornerShape(4.dp)
    val selectionBoxBorderColor = MaterialTheme.colorScheme.outline

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape) // Clip the whole row
            .border(1.dp, borderColor, shape) // Border around the whole row based on selection
            .background(backgroundColor, shape) // Background for the whole row based on selection
            .clickable { onSelected() }
            .padding(vertical = 12.dp, horizontal = 10.dp), // Adjust padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Selection Box (displays 'X' when selected)
        Box(
            modifier = Modifier
                .size(32.dp) // The square box for the X
                .border(1.5.dp, selectionBoxBorderColor, selectionBoxShape),
            contentAlignment = Alignment.Center
        ) {
            // --- Display "X" if selected ---
            if (isSelected) {
                Text(
                    text = "X",
                    fontSize = 22.sp, // Adjust size as needed
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            // --- Else: Box is empty ---
        }
        Spacer(modifier = Modifier.width(12.dp))
        // Answer Text
        Text(
            text = answer.text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f) // Take remaining space
        )
    }
}


// --- Previews ---
@Preview(showBackground = true, widthDp = 380)
@Composable
fun ReferendumScreenPreview() {
    // Sample Data for Preview
    val sampleQuestion = "Съгласни ли сте българският лев да бъде единствена официална валута в България до 2043 г.?"
    val sampleAnswers = listOf(
        ReferendumAnswer(id = 1, text = "ДА"),
        ReferendumAnswer(id = 2, text = "НЕ")
    )

    AppTheme {
        ReferendumScreen(
            referendumQuestion = sampleQuestion,
            answers = sampleAnswers,
            onNavigateBack = {},
            onReviewVote = { answerId ->
                println("Review Vote -> Answer ID: $answerId")
            }
        )
    }
}