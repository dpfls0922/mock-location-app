package com.example.mocklocation.presentation.map.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mocklocation.presentation.mock.MockingState

@Composable
fun MockGpsButton(
    onStartMock: () -> Unit,
    onStopMock: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = onStartMock,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mock GPS 전송 시작")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onStopMock,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF44336)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mock GPS 전송 중지")
        }
    }
}

data class GpsStatusProps(
    val containerColor: Color,
    val icon: ImageVector,
    val tintColor: Color,
    val statusText: String,
    val textColor: Color
)

@Composable
fun MockGpsStatus(mockingState: MockingState) {
    val statusProps = when (mockingState) {
        MockingState.RUNNING -> GpsStatusProps(
            containerColor = Color(0xFFE8F5E8),
            icon = Icons.Default.PlayCircle,
            tintColor = Color(0xFF4CAF50),
            statusText = "Mock GPS 주행 중",
            textColor = Color(0xFF4CAF50)
        )
        MockingState.PAUSED -> GpsStatusProps(
            containerColor = Color(0xFFFFF3E0),
            icon = Icons.Default.Stop,
            tintColor = Color(0xFFF57F17),
            statusText = "Mock GPS 일시 정지",
            textColor = Color(0xFFF57F17)
        )
        MockingState.IDLE -> GpsStatusProps(
            containerColor = Color(0xFFE0E0E0),
            icon = Icons.Default.DoNotDisturbOn,
            tintColor = Color(0xFF616161),
            statusText = "Mock GPS 주행 전",
            textColor = Color(0xFF616161)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = statusProps.containerColor
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(statusProps.icon, contentDescription = null, tint = statusProps.tintColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = statusProps.statusText, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = statusProps.textColor)
        }
    }
}