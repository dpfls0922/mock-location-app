package com.example.mocklocation.presentation.map.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.mocklocation.presentation.map.MapViewModel
import com.example.mocklocation.presentation.mock.MockLocationViewModel
import com.example.mocklocation.presentation.route.RouteGuidanceViewModel
import com.example.mocklocation.presentation.search.SearchViewModel

@Composable
fun ExpandableGpsMockCard(
    mockViewModel: MockLocationViewModel,
    searchViewModel: SearchViewModel,
    routeGuidanceViewModel: RouteGuidanceViewModel,
    mapViewModel: MapViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLocationForRoute by searchViewModel.selectedLocationForRoute.collectAsState()

    LaunchedEffect(selectedLocationForRoute) {
        if (selectedLocationForRoute != null) {
            expanded = true
        }
    }

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
                    .width(400.dp)
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                mockViewModel = mockViewModel,
                searchViewModel = searchViewModel,
                routeGuidanceViewModel = routeGuidanceViewModel,
                mapViewModel = mapViewModel
            )
        }
    }
}