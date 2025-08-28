package com.example.mocklocation.presentation.map.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FloatingButtons(
    onClickMyLocation: () -> Unit,
    onClickSelectCsvFile: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FloatingActionButton(
            onClick = onClickMyLocation,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "현위치 조회")
        }
        FloatingActionButton(
            onClick = onClickSelectCsvFile,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Upload, contentDescription = "CSV 파일 업로드")
        }
    }
}

@Preview
@Composable
fun FloatingButtonsPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FloatingActionButton(
            onClick = { }
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "현위치 조회")
        }
        FloatingActionButton(
            onClick = { }
        ) {
            Icon(Icons.Default.Upload, contentDescription = "CSV 파일 업로드")
        }
    }
}