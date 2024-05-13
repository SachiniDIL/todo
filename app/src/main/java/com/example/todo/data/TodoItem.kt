package com.example.todo.data

data class TodoItem(
    val todoID: Int,
    val todoName: String,
    val priority: Int, // Include priority parameter in the constructor
    val status: String = "processing"
)
