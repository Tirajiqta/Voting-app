package com.example.android.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferendumScreen(
    referendumQuestion: String,
    answers: List<ReferendumAnswer>,
    onNavigateBack: () -> Unit,
    onReviewVote: (selectedAnswerId: Int) -> Unit
) {
    var selectedAnswerId by remember { mutableStateOf<Int?>(null) }

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
                        selectedAnswerId?.let { answerId ->
                            onReviewVote(answerId)
                        }
                    },
                    enabled = selectedAnswerId != null,
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("ПРЕГЛЕД", fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = referendumQuestion,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Justify,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            items(answers, key = { it.id }) { answer ->
                val isSelected = answer.id == selectedAnswerId
                ReferendumAnswerItem(
                    answer = answer,
                    isSelected = isSelected,
                    onSelected = {
                        selectedAnswerId = answer.id
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ReferendumAnswerItem(
    answer: ReferendumAnswer,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val shape = RoundedCornerShape(8.dp) // Slightly more rounded? Adjust as needed.

    val selectionBoxShape = RoundedCornerShape(4.dp)
    val selectionBoxBorderColor = MaterialTheme.colorScheme.outline

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(backgroundColor, shape)
            .clickable { onSelected() }
            .padding(vertical = 12.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(1.5.dp, selectionBoxBorderColor, selectionBoxShape),
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
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = answer.text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}


@Preview(showBackground = true, widthDp = 380)
@Composable
fun ReferendumScreenPreview() {
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