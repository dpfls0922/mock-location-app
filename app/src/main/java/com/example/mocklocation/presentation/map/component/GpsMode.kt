package com.example.mocklocation.presentation.map.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class GpsMode { GPS, DR }

@Composable
fun GPSMode(
    selectedMode: GpsMode,
    onModeSelected: (GpsMode) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "GPS 모드",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                onClick = { onModeSelected(GpsMode.GPS) },
                label = { Text("GPS 모드") },
                selected = selectedMode == GpsMode.GPS,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Gray.copy(alpha = 0.1f),
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            FilterChip(
                onClick = { onModeSelected(GpsMode.DR) },
                label = { Text("DR 모드") },
                selected = selectedMode == GpsMode.DR,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Gray.copy(alpha = 0.1f),
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
