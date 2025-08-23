package com.example.mocklocation.data.datasource

import android.content.Context
import android.net.Uri
import com.example.mocklocation.data.parser.CsvGpsParser
import com.example.mocklocation.di.IoDispatcher
import com.example.mocklocation.domain.entity.CsvFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val csvParser: CsvGpsParser,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FileDataSource {
    override suspend fun uploadFile(uri: Uri, fileName: String): CsvFile = withContext(ioDispatcher) {
        Timber.d("[파일 업로드 시작] 파일명: $fileName, URI: $uri")
        val inputStream = context.contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("URI를 열 수 없습니다: $uri")
        val gpsPoints = csvParser.parse(inputStream)

        Timber.d("파싱 완료: GPS 포인트 총 ${gpsPoints.size}개 발견")
        gpsPoints.forEachIndexed { index, point ->
            Timber.d("    [$index] 위도: ${point.latitude}, 경도: ${point.longitude}, 속도: ${point.speed}, 정확도: ${point.accuracy}")
        }

        val csvFile = CsvFile(
            id = UUID.randomUUID().toString(),
            name = fileName,
            uri = uri,
            gpsPoints = gpsPoints
        )

        Timber.d("[업로드 완료] CsvFile 객체 생성 ID: ${csvFile.id}, 파일명: ${csvFile.name}")
        csvFile
    }
}