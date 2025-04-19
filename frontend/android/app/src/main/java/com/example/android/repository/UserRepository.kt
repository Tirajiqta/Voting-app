package com.example.android.repository

import com.example.android.api.VotingApi
import com.example.android.dao.UserDao
import com.example.android.dto.request.DocumentRequestDTO
import com.example.android.dto.request.LoginRequest
import com.example.android.dto.response.DocumentResponseDTO
import com.example.android.dto.response.LoginResponse
import com.example.android.entity.UserEntity

class UserRepository(private val userDao: UserDao) {

    fun login(request: LoginRequest, callback: (Result<LoginResponse>) -> Unit) {
        VotingApi.login(request, object : VotingApi.Callback<LoginResponse> {
            override fun onSuccess(response: LoginResponse) {
                callback(Result.success(response))
            }

            override fun onFailure(error: Throwable) {
                callback(Result.failure(error))
            }
        })
    }

    fun getUserById(userId: Long): UserEntity? {
        return userDao.getById(userId)
    }

    fun saveUser(user: UserEntity) {
        userDao.insert(user)
    }

    fun addDocument(request: DocumentRequestDTO, token: String, callback: (Result<DocumentResponseDTO>) -> Unit) {
        VotingApi.addDocument(token, request, object : VotingApi.Callback<DocumentResponseDTO> {
            override fun onSuccess(response: DocumentResponseDTO) {
                callback(Result.success(response))
            }

            override fun onFailure(error: Throwable) {
                callback(Result.failure(error))
            }
        })
    }
}