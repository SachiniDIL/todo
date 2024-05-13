package com.example.todo.data

data class TodoItem(
    val id: Int,
    var todoName: String,
    var priority: Int,
    var isDone: Int
)

