package com.example.mocklocation.presentation.ui.common

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberPermissionLauncher(
    onPermissionResult: (Boolean) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = onPermissionResult
)

@Composable
fun rememberFileSelectorLauncher(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onFileSelected: suspend (Uri) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri ->
    uri?.let { selectedUri ->
        coroutineScope.launch {
            onFileSelected(selectedUri)
        }
    }
}