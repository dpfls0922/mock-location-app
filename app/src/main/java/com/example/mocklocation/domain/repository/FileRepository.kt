package com.example.mocklocation.domain.repository

import com.example.mocklocation.domain.entity.CsvFile
import android.net.Uri

interface FileRepository {
    suspend fun uploadCsvFile(
        uri: Uri,
        onProgress: (Float) -> Unit
    ): CsvFile
}