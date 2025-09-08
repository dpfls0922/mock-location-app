package com.example.mocklocation.data.dto.common

interface BaseResponse {
    val header: ResponseHeader
}

fun BaseResponse.isSuccess(): Boolean = this.header.errorCode == "000000"

fun BaseResponse.errorMessageOrDefault(): String =
    this.header.errorMessage.ifBlank { "알 수 없는 오류가 발생했습니다." }