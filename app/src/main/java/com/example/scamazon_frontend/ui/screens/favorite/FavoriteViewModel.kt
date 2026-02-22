package com.example.scamazon_frontend.ui.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.favorite.FavoriteItemDto
import com.example.scamazon_frontend.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: FavoriteRepository) : ViewModel() {

    private val _favoritesState = MutableStateFlow<Resource<List<FavoriteItemDto>>?>(null)
    val favoritesState: StateFlow<Resource<List<FavoriteItemDto>>?> = _favoritesState.asStateFlow()

    /** Set of product IDs currently favorited by this user */
    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    init {
        loadFavoriteIds()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _favoritesState.value = Resource.Loading()
            _favoritesState.value = repository.getFavorites()
        }
    }

    fun loadFavoriteIds() {
        viewModelScope.launch {
            val result = repository.getFavoriteIds()
            if (result is Resource.Success) {
                _favoriteIds.value = result.data?.toSet() ?: emptySet()
            }
        }
    }

    fun toggleFavorite(productId: Int) {
        // Optimistic UI update
        val currentIds = _favoriteIds.value.toMutableSet()
        val wasFavorited = currentIds.contains(productId)
        if (wasFavorited) currentIds.remove(productId) else currentIds.add(productId)
        _favoriteIds.value = currentIds

        viewModelScope.launch {
            val result = repository.toggleFavorite(productId)
            if (result is Resource.Error) {
                // Revert on error
                val revertedIds = _favoriteIds.value.toMutableSet()
                if (wasFavorited) revertedIds.add(productId) else revertedIds.remove(productId)
                _favoriteIds.value = revertedIds
            }
        }
    }

    fun isFavorited(productId: Int): Boolean {
        return _favoriteIds.value.contains(productId)
    }
}
