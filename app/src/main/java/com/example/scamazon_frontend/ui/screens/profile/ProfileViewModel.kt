package com.example.scamazon_frontend.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.profile.ProfileDataDto
import com.example.scamazon_frontend.data.models.profile.UpdateProfileRequest
import com.example.scamazon_frontend.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<ProfileDataDto>>(Resource.Loading())
    val profileState: StateFlow<Resource<ProfileDataDto>> = _profileState.asStateFlow()

    private val _updateState = MutableStateFlow<Resource<ProfileDataDto>?>(null)
    val updateState: StateFlow<Resource<ProfileDataDto>?> = _updateState.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = Resource.Loading()
            _profileState.value = repository.getProfile()
        }
    }

    fun updateProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            _updateState.value = Resource.Loading()
            val result = repository.updateProfile(request)
            _updateState.value = result
            if (result is Resource.Success) {
                // Refresh profile data after successful update
                _profileState.value = result
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = null
    }
}
