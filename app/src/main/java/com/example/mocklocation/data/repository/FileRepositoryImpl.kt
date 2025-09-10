package com.example.mocklocation.data.repository

import android.net.Uri
import com.example.mocklocation.data.datasource.FileDataSource
import com.example.mocklocation.domain.entity.CsvFile
import com.example.mocklocation.domain.repository.FileRepository
import com.example.mocklocation.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepositoryImpl @Inject constructor(
    private val fileDataSource: FileDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FileRepository {
    override suspend fun uploadCsvFile(
        uri: Uri,
        onProgress: (Float) -> Unit
    ): CsvFile = withContext(ioDispatcher) {
        fileDataSource.uploadFile(uri, onProgress)
    }
}