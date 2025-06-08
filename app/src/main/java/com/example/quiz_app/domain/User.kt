package com.example.quiz_app.domain

data class User(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val createdAt: String,
    val updatedAt: String
)