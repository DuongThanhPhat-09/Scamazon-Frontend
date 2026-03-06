package com.example.scamazon_frontend.ui.screens.profile

import android.content.Context
import android.net.Uri
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<ProfileDataDto>>(Resource.Loading())
    val profileState: StateFlow<Resource<ProfileDataDto>> = _profileState.asStateFlow()

    private val _updateState = MutableStateFlow<Resource<ProfileDataDto>?>(null)
    val updateState: StateFlow<Resource<ProfileDataDto>?> = _updateState.asStateFlow()

    private val _uploadAvatarState = MutableStateFlow<Resource<ProfileDataDto>?>(null)
    val uploadAvatarState: StateFlow<Resource<ProfileDataDto>?> = _uploadAvatarState.asStateFlow()

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

    fun uploadAvatar(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uploadAvatarState.value = Resource.Loading()
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    _uploadAvatarState.value = Resource.Error("Cannot read file")
                    return@launch
                }

                val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                val bytes = inputStream.readBytes()
                inputStream.close()

                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", "avatar.jpg", requestBody)

                val result = repository.uploadAvatar(part)
                _uploadAvatarState.value = result
                if (result is Resource.Success) {
                    _profileState.value = result
                }
            } catch (e: Exception) {
                _uploadAvatarState.value = Resource.Error(e.message ?: "Upload failed")
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = null
    }

    fun resetUploadAvatarState() {
        _uploadAvatarState.value = null
    }
}
