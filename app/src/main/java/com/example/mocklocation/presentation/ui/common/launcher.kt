package com.example.mocklocation.presentation.ui.common

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

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
    allowedExtensions: List<String> = listOf("csv", "txt", "dlt"),
    onFileSelected: suspend (Uri) -> Unit
) : ActivityResultLauncher<String> {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            if (isExtensionAllowed(selectedUri, allowedExtensions, context)) {
                Timber.d("선택된 파일 확장자: ${selectedUri.getFileExtensionByName(context)}, 처리 시작")
                coroutineScope.launch { onFileSelected(selectedUri) }
            } else {
                Timber.d("허용되지 않은 확장자: ${selectedUri.getFileExtensionByName(context)}")
                showUnsupportedFileToast(context)
            }
        }
    }
    return launcher
}

private fun isExtensionAllowed(uri: Uri, allowedExtensions: List<String>, context: Context): Boolean {
    val ext = uri.getFileExtensionByName(context)?.lowercase() ?: return false
    return allowedExtensions.contains(ext)
}

private fun showUnsupportedFileToast(context: Context) {
    Toast.makeText(context, "지원하지 않는 파일 형식입니다.", Toast.LENGTH_SHORT).show()
}