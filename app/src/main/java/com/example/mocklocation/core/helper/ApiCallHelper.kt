package com.example.mocklocation.core.helper

import com.example.mocklocation.core.model.ApiResult
import com.example.mocklocation.data.dto.common.BaseResponse
import com.example.mocklocation.data.dto.common.errorMessageOrDefault
import com.example.mocklocation.data.dto.common.isSuccess
import retrofit2.HttpException
import java.io.IOException

suspend fun <T : BaseResponse, R> safeApiCall(
    apiCall: suspend () -> T,
    transformSuccess: suspend (T) -> R
): ApiResult<R> {
    return try {
        val response = apiCall()
        if (response.isSuccess()) {
            ApiResult.Success(transformSuccess(response))
        } else {
            ApiResult.Error(response.errorMessageOrDefault())
        }
    } catch (e: IOException) {
        ApiResult.Error("네트워크 연결에 문제가 있습니다. 다시 시도해 주세요.")
    } catch (e: HttpException) {
        ApiResult.Error("서버와 통신 중 오류가 발생했습니다. 상태 코드: ${e.code()}")
    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage ?: "알 수 없는 오류가 발생했습니다.")
    }
}