package com.example.mocklocation.presentation.map.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SpeedSection(
    selectedSpeed: String,
    onSpeedSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "이동 속도",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpeedOption(
                label = "보행",
                speed = "5km/h",
                selected = selectedSpeed == "5km/h",
                onClick = { onSpeedSelected("5km/h") },
                modifier = Modifier.weight(1f)
            )
            SpeedOption(
                label = "차량 저속",
                speed = "30km/h",
                selected = selectedSpeed == "30km/h",
                onClick = { onSpeedSelected("30km/h") },
                modifier = Modifier.weight(1f)
            )
            SpeedOption(
                label = "차량 고속",
                speed = "120km/h",
                selected = selectedSpeed == "120km/h",
                onClick = { onSpeedSelected("120km/h") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SpeedOption(
    label: String,
    speed: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else Color.Gray.copy(alpha = 0.05f)
        ),
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
            )
            Text(
                text = speed,
                fontSize = 10.sp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
    }
}