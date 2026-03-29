package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.safeApiCall
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.profile.ProfileDataDto
import com.example.scamazon_frontend.data.models.profile.UpdateProfileRequest
import com.example.scamazon_frontend.data.remote.ProfileService
import okhttp3.MultipartBody

class ProfileRepository(private val profileService: ProfileService) {

    suspend fun getProfile(): Resource<ProfileDataDto> {
        return safeApiCall { profileService.getProfile() }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Resource<ProfileDataDto> {
        return safeApiCall { profileService.updateProfile(request) }
    }

    suspend fun uploadAvatar(file: MultipartBody.Part): Resource<ProfileDataDto> {
        return safeApiCall { profileService.uploadAvatar(file) }
    }
}
