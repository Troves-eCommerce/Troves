package com.troves.data.source.remote.ai

import com.troves.data.source.remote.ai.dto.AiChatRequestDto
import com.troves.data.source.remote.ai.dto.AiChatResponseDto
import com.troves.domain.utils.Result

interface AiDataSource {
    suspend fun chat(request: AiChatRequestDto): Result<AiChatResponseDto>
}

class AiDataSourceImpl(
    private val apiService: AiApiService,
) : AiDataSource {
    override suspend fun chat(request: AiChatRequestDto): Result<AiChatResponseDto> =
        apiService.chat(request)
}
