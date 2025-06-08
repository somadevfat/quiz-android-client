package com.example.quiz_app.data.remote

import com.example.quiz_app.data.remote.dto.AuthResponse
import com.example.quiz_app.data.remote.dto.LoginRequest
import com.example.quiz_app.data.remote.dto.RefreshTokenRequest
import com.example.quiz_app.data.remote.dto.RegisterRequest
import com.example.quiz_app.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
    
    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Unit
    
    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): UserDto
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): AuthResponse
}