package com.example.scamazon_frontend.core.network

import com.example.scamazon_frontend.core.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

/**
 * Shared safe API call utility.
 * Eliminates duplicate safeApiCall() methods across all repositories.
 *
 * Handles:
 * - Successful responses with data
 * - Successful responses without data (message-only)
 * - ASP.NET ValidationProblemDetails error parsing
 * - Generic error message parsing
 * - Network exceptions
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<ApiResponse<T>>
): Resource<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    if (body.data != null) {
                        Resource.Success(body.data)
                    } else {
                        Resource.Error(body.message ?: "No data found")
                    }
                } else {
                    Resource.Error(body?.message ?: "Unknown error")
                }
            } else {
                Resource.Error(parseErrorBody(response))
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}

/**
 * Variant for endpoints that return success/message but no typed data.
 * Returns the message string on success instead of the data object.
 */
suspend fun <T> safeApiCallMessage(
    apiCall: suspend () -> Response<ApiResponse<T>>
): Resource<String> {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Resource.Success(body.message ?: "Thành công")
                } else {
                    Resource.Error(body?.message ?: "Có lỗi xảy ra")
                }
            } else {
                Resource.Error(parseErrorBody(response))
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}

/**
 * Variant that allows null data on success (e.g., for delete endpoints).
 * Returns Resource.Success even when body.data is null.
 */
@Suppress("UNCHECKED_CAST")
suspend fun <T> safeApiCallNullable(
    apiCall: suspend () -> Response<ApiResponse<T>>
): Resource<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Resource.Success(body.data as T)
                } else {
                    Resource.Error(body?.message ?: "Unknown error")
                }
            } else {
                Resource.Error(parseErrorBody(response))
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}

/**
 * Parses the error body from a failed HTTP response.
 * Supports:
 * - ASP.NET ValidationProblemDetails format: { "errors": { "Field": ["msg"] } }
 * - Standard format: { "message": "..." } or { "Message": "..." }
 * - Title fallback: { "title": "..." }
 */
private fun <T> parseErrorBody(response: Response<T>): String {
    var errorMsg = "API Error: ${response.code()}"
    response.errorBody()?.string()?.let { errorBodyStr ->
        try {
            val json = JSONObject(errorBodyStr)

            // Check for validation errors (ASP.NET ValidationProblemDetails)
            var specificErrorMsg: String? = null
            val errorsObj = json.optJSONObject("errors") ?: json.optJSONObject("Errors")

            if (errorsObj != null) {
                val keys = errorsObj.keys()
                if (keys.hasNext()) {
                    val firstKey = keys.next()
                    val messagesArray = errorsObj.optJSONArray(firstKey)
                    if (messagesArray != null && messagesArray.length() > 0) {
                        val fieldMsg = messagesArray.optString(0)
                        if (fieldMsg.isNotEmpty()) specificErrorMsg = fieldMsg
                    }
                }
            }

            // Get generic message or title
            val genericMsg = json.optString("message")
                .ifEmpty { json.optString("Message") }
                .ifEmpty { json.optString("title") }

            // Use specific error if available, else fallback to generic message
            if (!specificErrorMsg.isNullOrEmpty()) {
                errorMsg = specificErrorMsg
            } else if (genericMsg.isNotEmpty()) {
                errorMsg = genericMsg
            }
        } catch (_: Exception) {
            // Fallback to HTTP code message
        }
    }
    return errorMsg
}
