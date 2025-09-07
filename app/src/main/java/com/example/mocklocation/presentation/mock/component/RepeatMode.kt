package com.example.mocklocation.presentation.mock.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class RepeatMode { INFINITE, COUNT }

@Composable
fun RepeatMode(
    repeatMode: RepeatMode,
    repeatCount: Int,
    onRepeatModeChanged: (RepeatMode) -> Unit,
    onRepeatCountChanged: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "반복 모드",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                onClick = { onRepeatModeChanged(RepeatMode.INFINITE) },
                label = { Text("무한 반복") },
                selected = repeatMode == RepeatMode.INFINITE,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Gray.copy(alpha = 0.1f),
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            FilterChip(
                onClick = { onRepeatModeChanged(RepeatMode.COUNT) },
                label = { Text("횟수 반복") },
                selected = repeatMode == RepeatMode.COUNT,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Gray.copy(alpha = 0.1f),
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        if (repeatMode == RepeatMode.COUNT) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "반복 횟수:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (repeatCount > 1) onRepeatCountChanged(repeatCount - 1) },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "감소",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = repeatCount.toString(),
                        modifier = Modifier.width(30.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    IconButton(
                        onClick = { if (repeatCount < 99) onRepeatCountChanged(repeatCount + 1) },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "증가",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
