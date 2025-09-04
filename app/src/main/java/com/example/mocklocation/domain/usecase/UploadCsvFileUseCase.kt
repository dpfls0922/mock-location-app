package com.example.mocklocation.domain.usecase

import android.net.Uri
import com.example.mocklocation.domain.entity.CsvFile
import com.example.mocklocation.domain.repository.FileRepository
import com.example.mocklocation.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UploadCsvFileUseCase @Inject constructor(
    private val fileRepository: FileRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun execute(uri: Uri): CsvFile = withContext(ioDispatcher) {
        fileRepository.uploadCsvFile(uri)
    }
}