package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.category.CategoryDto
import com.example.scamazon_frontend.data.models.product.ProductPaginationResponse
import com.example.scamazon_frontend.data.remote.CategoryService
import com.example.scamazon_frontend.data.remote.ProductService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

class HomeRepository(
    private val productService: ProductService,
    private val categoryService: CategoryService
) {

    suspend fun getCategories(): Resource<List<CategoryDto>> {
        return safeApiCall { categoryService.getCategories() }
    }

    suspend fun getProducts(
        page: Int? = null,
        limit: Int? = null,
        categoryId: Int? = null,
        sort: String? = null
    ): Resource<ProductPaginationResponse> {
        return safeApiCall { productService.getProducts(page, limit, categoryId, sort) }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): Resource<T> {
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
                    var errorMsg = "API Error: ${response.code()}"
                    response.errorBody()?.string()?.let {
                        try {
                            val json = JSONObject(it)
                            
                            // Check for validation errors object (ASP.NET ValidationProblemDetails)
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
                        } catch (e: Exception) {
                            // Fallback to HTTP code
                        }
                    }
                    Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Network error occurred")
            }
        }
    }
}
