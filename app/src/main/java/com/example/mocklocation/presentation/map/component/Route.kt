package com.example.mocklocation.presentation.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mocklocation.presentation.search.SearchResultUiModel

enum class RoutePointType(
    val label: String,
    val icon: ImageVector,
    val color: Color
) {
    START("출발", Icons.Default.PlayArrow, Color(0xFF4CAF50)),
    END("도착", Icons.Default.Flag, Color(0xFFF44336)),
    WAYPOINT("경유", Icons.Default.LocationOn, Color(0xFF2196F3))
}

data class RoutePoint(
    val type: RoutePointType,
    val location: SearchResultUiModel
)

@Composable
fun RouteSelectionDialog(
    selectedLocation: SearchResultUiModel,
    onDismiss: () -> Unit,
    onRouteTypeSelected: (RoutePointType) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                SelectedPoiCard(selectedLocation)

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    RouteTypeOptionButton(
                        routeType = RoutePointType.START,
                        onClick = { onRouteTypeSelected(RoutePointType.START) },
                        modifier = Modifier.weight(1f)
                    )
                    RouteTypeOptionButton(
                        routeType = RoutePointType.WAYPOINT,
                        onClick = { onRouteTypeSelected(RoutePointType.WAYPOINT) },
                        modifier = Modifier.weight(1f)
                    )
                    RouteTypeOptionButton(
                        routeType = RoutePointType.END,
                        onClick = { onRouteTypeSelected(RoutePointType.END) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } }
    )
}

@Composable
private fun SelectedPoiCard(poi: SearchResultUiModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.6f)) {
                Text(
                    text = poi.name,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = poi.roadAddress,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            poi.imgUrl?.let { img ->
                AsyncImage(
                    model = img,
                    contentDescription = "${poi.name} 이미지",
                    modifier = Modifier
                        .weight(0.3f)
                        .height(76.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun RouteTypeOptionButton(
    routeType: RoutePointType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .widthIn(min = 50.dp, max = 60.dp),
        shape = RoundedCornerShape(40.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(routeType.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = routeType.icon,
                    contentDescription = routeType.label,
                    tint = routeType.color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = routeType.label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
        }
    }
}

@Composable
fun RoutePointsList(
    routePoints: List<RoutePoint>,
    onRemovePoint: (RoutePoint) -> Unit,
    onReset: () -> Unit,
    onRouteGuidance: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (routePoints.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                routePoints.forEach { routePoint ->
                    RoutePointItem(
                        routePoint = routePoint,
                        onRemove = { onRemovePoint(routePoint) }
                    )
                }
            }

            val hasStartAndEnd = routePoints.any { it.type == RoutePointType.START } &&
                    routePoints.any { it.type == RoutePointType.END }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onReset,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                            contentColor =  MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(text = "초기화")
                    }
                    Button(
                        onClick = onRouteGuidance,
                        enabled = hasStartAndEnd,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(text = "안내")
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutePointItem(
    routePoint: RoutePoint,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(routePoint.type.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = routePoint.type.icon,
                    contentDescription = routePoint.type.label,
                    tint = routePoint.type.color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = routePoint.type.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = routePoint.type.color,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = routePoint.location.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RouteSelectionDialogPreview() {
    val sampleLocation = SearchResultUiModel(
        pkey = "",
        name = "서울역",
        roadAddress = "서울특별시 중구",
        xPos = 0,
        yPos = 0,
        distanceText = "12km",
        hasParking = true,
        imgUrl = "https://i.pinimg.com/originals/b7/45/ef/b745ef9bb3bd41f685850cdd85a19338.jpg",
        detailUrl = ""
    )

    RouteSelectionDialog(
        selectedLocation = sampleLocation,
        onDismiss = {},
        onRouteTypeSelected = {}
    )
}