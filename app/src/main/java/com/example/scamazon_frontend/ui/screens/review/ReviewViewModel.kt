package com.example.scamazon_frontend.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.review.ReviewDto
import com.example.scamazon_frontend.data.models.review.ReviewListDataDto
import com.example.scamazon_frontend.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ReviewRepository) : ViewModel() {

    private val _reviewsState = MutableStateFlow<Resource<ReviewListDataDto>?>(null)
    val reviewsState: StateFlow<Resource<ReviewListDataDto>?> = _reviewsState.asStateFlow()

    private val _createReviewState = MutableStateFlow<Resource<ReviewDto>?>(null)
    val createReviewState: StateFlow<Resource<ReviewDto>?> = _createReviewState.asStateFlow()

    private val _selectedRating = MutableStateFlow(0)
    val selectedRating: StateFlow<Int> = _selectedRating.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    fun loadReviews(productId: Int, page: Int = 1, limit: Int = 10, rating: Int? = null) {
        viewModelScope.launch {
            _reviewsState.value = Resource.Loading()
            _reviewsState.value = repository.getProductReviews(productId, page, limit, rating)
        }
    }

    fun submitReview(productId: Int) {
        if (_selectedRating.value == 0) return
        viewModelScope.launch {
            _createReviewState.value = Resource.Loading()
            _createReviewState.value = repository.createReview(
                productId,
                _selectedRating.value,
                _comment.value.ifBlank { null }
            )
        }
    }

    fun setRating(rating: Int) {
        _selectedRating.value = rating
    }

    fun setComment(text: String) {
        _comment.value = text
    }

    fun resetCreateState() {
        _createReviewState.value = null
        _selectedRating.value = 0
        _comment.value = ""
    }
}
