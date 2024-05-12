package com.example.todo_mobile_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val id: Int = 1,
    val name: String?
)
