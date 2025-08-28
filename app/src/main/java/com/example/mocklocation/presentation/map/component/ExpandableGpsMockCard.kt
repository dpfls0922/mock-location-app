package com.example.mocklocation.presentation.map.component

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun ExpandableGpsMockCard(
    onStartMock: () -> Unit,
    onStopMock: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .zIndex(1f)
        ) {
            Icon(
                if (expanded) Icons.AutoMirrored.Filled.KeyboardArrowLeft else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = if (expanded) "접기" else "펼치기",
            )
        }

        if (expanded) {
            GPSMockCard(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.3f)
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                onStartMock,
                onStopMock
            )
        }
    }
}