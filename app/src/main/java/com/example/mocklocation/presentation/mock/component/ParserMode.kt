package com.example.mocklocation.presentation.mock.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mocklocation.presentation.route.TvasParserMode

@Composable
fun TvasParserToggle(
    mode: TvasParserMode,
    onModeChange: (TvasParserMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .height(32.dp)
            .background(
                color = colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(modifier = Modifier.height(32.dp)) {
            ToggleItem(
                text = "Native",
                isSelected = mode == TvasParserMode.NATIVE,
                onClick = { onModeChange(TvasParserMode.NATIVE) },
                colorScheme = colorScheme
            )
            ToggleItem(
                text = "Kotlin",
                isSelected = mode == TvasParserMode.KOTLIN,
                onClick = { onModeChange(TvasParserMode.KOTLIN) },
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
private fun ToggleItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorScheme: androidx.compose.material3.ColorScheme
) {
    Box(
        modifier = Modifier
            .width(60.dp)
            .height(28.dp)
            .padding(2.dp)
            .background(
                color = if (isSelected) colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant
        )
    }
}