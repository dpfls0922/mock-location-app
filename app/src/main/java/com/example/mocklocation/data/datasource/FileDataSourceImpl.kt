package com.example.mocklocation.data.datasource

import android.content.Context
import android.net.Uri
import com.example.mocklocation.core.helper.CountingInputStream
import com.example.mocklocation.di.CsvParser
import com.example.mocklocation.di.IoDispatcher
import com.example.mocklocation.domain.entity.CsvFile
import com.example.mocklocation.domain.parser.GpsDataParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CsvParser private val parser: GpsDataParser<InputStream>
) : FileDataSource {

    override suspend fun uploadFile(uri: Uri, onProgress: (Float) -> Unit): CsvFile = withContext(ioDispatcher) {
        val fileName = getFileNameFromUri(uri)
        Timber.d("[파일 업로드 시작] 파일명: $fileName, URI: $uri")

        val inputStream = openInputStream(uri)
        val totalBytes = inputStream.available().toFloat()

        val countedInputStream = CountingInputStream(inputStream) { bytesRead ->
            val progress = (bytesRead / totalBytes).coerceIn(0f, 1f)
            onProgress(progress)
        }

        val gpsPoints = parser.parse(countedInputStream)
        logGpsPoints(gpsPoints)

        val csvFile = CsvFile(
            id = UUID.randomUUID().toString(),
            name = fileName,
            uri = uri,
            gpsPoints = gpsPoints
        )

        Timber.d("[업로드 완료] CsvFile 객체 생성 ID: ${csvFile.id}, 파일명: ${csvFile.name}")
        csvFile
    }

    private fun openInputStream(uri: Uri): InputStream =
        context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("URI를 열 수 없습니다: $uri")

    private fun getFileNameFromUri(uri: Uri): String {
        val projection = arrayOf(android.provider.OpenableColumns.DISPLAY_NAME)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        }
        return uri.lastPathSegment ?: "unknown_file"
    }

    private fun logGpsPoints(gpsPoints: List<com.example.mocklocation.domain.entity.GpsPoint>) {
        Timber.d("파싱 완료: GPS 포인트 총 ${gpsPoints.size}개 발견")
        gpsPoints.forEachIndexed { index, point ->
            Timber.d("[$index] 위도: ${point.latitude}, 경도: ${point.longitude}, 속도: ${point.speed}, 정확도: ${point.hAcc}")
        }
    }
}