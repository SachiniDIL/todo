package com.example.todo_mobile_app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.Date

@Dao
interface TodoItemDao {

    @Insert
    suspend fun insertTodoItem(todoItem: TodoItem): Long

    @Update
    suspend fun updateTodoItem(todoItem: TodoItem)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItem)

    @Query("SELECT * FROM todoitem")
    suspend fun getAllTodoItems(): List<TodoItem>

    @Query("SELECT * FROM todoitem WHERE todoID = :id")
    suspend fun getTodoItemById(id: Long): TodoItem?

    @Query("SELECT * FROM todoitem WHERE date = :timestamp")
    suspend fun getTodoItemsForToday(timestamp: Long): List<TodoItem>

}
