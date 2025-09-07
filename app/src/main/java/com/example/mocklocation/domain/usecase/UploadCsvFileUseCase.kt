package com.example.mocklocation.domain.usecase

import android.net.Uri
import com.example.mocklocation.domain.entity.CsvFile
import com.example.mocklocation.domain.repository.FileRepository
import javax.inject.Inject

class UploadCsvFileUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend fun execute(uri: Uri): Result<CsvFile> = runCatching {
        fileRepository.uploadCsvFile(uri)
    }
}