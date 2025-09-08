package com.example.mocklocation.presentation.search

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchStatusHandler(
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiEvent = searchViewModel.uiEvent

    LaunchedEffect(Unit) {
        uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}