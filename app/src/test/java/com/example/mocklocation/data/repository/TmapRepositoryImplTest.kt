package com.example.mocklocation.data.repository

import com.example.mocklocation.data.datasource.TmapDataSource
import com.example.mocklocation.data.dto.response.AutoCompleteResponse
import com.example.mocklocation.data.dto.response.Response
import com.example.mocklocation.data.dto.response.Suggestion
import com.example.mocklocation.data.factory.GpsParserFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TmapRepositoryImplTest {
    private lateinit var repository: TmapRepositoryImpl
    private lateinit var mockDataSource: TmapDataSource
    private lateinit var mockParserFactory: GpsParserFactory

    @BeforeEach
    fun setup() {
        mockDataSource = mockk()
        mockParserFactory = mockk()
        repository = TmapRepositoryImpl(
            dataSource = mockDataSource,
            parserFactory = mockParserFactory,
            ioDispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    @DisplayName("자동완성 결과 정상 반환")
    fun getAutoCompleteSuggestionsTest() = runBlocking {
        val mockResponse = AutoCompleteResponse(
            response = Response(
                returnCode = 0,
                totalTime = 100.0,
                totalCount = 1,
                suggestions = listOf(Suggestion("서울", "서울역", "서울역 2번 출구"))
            )
        )
        coEvery { mockDataSource.getAutoCompleteSuggestions("서울") } returns mockResponse

        val result = repository.getAutoCompleteSuggestions("서울")
        assertEquals(1, result.size)
        assertEquals("서울", result.first().keyword)
    }
}