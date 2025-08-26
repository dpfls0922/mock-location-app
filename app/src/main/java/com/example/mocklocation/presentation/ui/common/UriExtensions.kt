package com.example.mocklocation.presentation.ui.common

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun Uri.getFileName(context: Context): String? {
    var name: String? = null
    val cursor = context.contentResolver.query(this, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        }
    }
    return name
}

fun Uri.getFileExtensionByName(context: Context): String? {
    val fileName = getFileName(context) ?: return null
    return fileName.substringAfterLast('.', "")
}