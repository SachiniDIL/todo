package com.example.todo.data

data class TodoItem(
    val todoID: Int,
    val todoName: String,
    val priority: Int,
    val status: String = "processing"
) {
    // Add a no-argument constructor with default values
    constructor() : this(0, "", 0)
}
