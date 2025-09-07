package com.example.mocklocation.presentation.mock.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.presentation.mock.MockingState
import timber.log.Timber

@Composable
fun MockGpsButton(
    mockingState: MockingState,
    gpsPoints: List<GpsPoint>,
    onStartMock: () -> Unit,
    onStopMock: () -> Unit,
    onResetMock: () -> Unit
) {
    val isEnabled = when (mockingState) {
        MockingState.IDLE -> gpsPoints.isNotEmpty()
        MockingState.PAUSED -> gpsPoints.isNotEmpty()
        MockingState.RUNNING -> gpsPoints.isNotEmpty()
    }
    LaunchedEffect(mockingState, gpsPoints) {
        Timber.d("[MockGpsButton]: mockingState=$mockingState, gpsPoints.isNotEmpty()=${gpsPoints.isNotEmpty()}, isEnabled=$isEnabled")
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = onStartMock,
            modifier = Modifier.fillMaxWidth(),
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                disabledContainerColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("전송")
        }

        Button(
            onClick = onStopMock,
            modifier = Modifier.fillMaxWidth(),
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                disabledContainerColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("일시정지")
        }

        Button(
            onClick = onResetMock,
            modifier = Modifier.fillMaxWidth(),
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF44336),
                disabledContainerColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("초기화")
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

@Composable
fun MockGpsProgressBar(
    progress: Int,
    modifier: Modifier = Modifier
) {
    val barHeight = 140.dp
    val barWidth = 12.dp
    val circleSize = 33.dp

    val progressRatio = progress / 100f
    val filledBarHeight = barHeight * progressRatio
    val iconOffsetY = (filledBarHeight - (circleSize / 2))

    Box(
        modifier = modifier
            .width(80.dp)
            .height(barHeight + 70.dp)
            .background(
                Color(0x60000000),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .width(barWidth)
                .height(barHeight)
                .align(Alignment.Center)
                .background(
                    Color(0x30FFFFFF),
                    RoundedCornerShape(6)
                )
        )

        Box(
            Modifier
                .width(barWidth)
                .height(filledBarHeight)
                .align(Alignment.BottomCenter)
                .background(
                    Color(0xFF1976D2),
                    shape = RoundedCornerShape(6)
                )
        )

        Box(
            Modifier
                .size(circleSize)
                .offset(y = -iconOffsetY)
                .background(
                    Color.White,
                    shape = CircleShape
                )
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Navigation,
                contentDescription = null,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(23.dp)
            )
        }

        val percentText = "${progress}%"
        Text(
            text = percentText,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .width(50.dp)
                .align(Alignment.TopCenter)
                .background(
                    Color(0x80000000),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMockGpsProgressBar() {
    Box(modifier = Modifier.fillMaxSize()) {
        MockGpsProgressBar(
            progress = 50,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 18.dp)
        )
    }
}