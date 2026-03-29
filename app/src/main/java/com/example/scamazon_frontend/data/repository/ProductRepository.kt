package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.safeApiCall
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.product.ProductDetailDto
import com.example.scamazon_frontend.data.remote.ProductService

class ProductRepository(private val productService: ProductService) {

    suspend fun getProductBySlug(slug: String): Resource<ProductDetailDto> {
        return safeApiCall { productService.getProductBySlug(slug) }
    }
}
