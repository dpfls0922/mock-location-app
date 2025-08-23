package com.example.mocklocation.data.datasource

import android.net.Uri
import com.example.mocklocation.domain.entity.CsvFile

interface FileDataSource {
    suspend fun uploadFile(uri: Uri, fileName: String): CsvFile
}
