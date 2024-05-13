package com.example.todo.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TodoViewModel(private val databaseHelper: DatabaseHelper) : ViewModel() {

    // LiveData to hold the list of todo items
    private val _todoList = MutableLiveData<List<TodoItem>>()
    val todoList: LiveData<List<TodoItem>> = _todoList

    init {
        loadTodoList()
    }

    fun loadTodoList() {
        // Load todo list from the database using the helper
        _todoList.value = fetchTasksFromSQLite()
    }

    fun deleteTask(taskId: Int) {
        // Delete task from the database using the helper
        val deletedRows = databaseHelper.deleteTask(taskId)
        if (deletedRows > 0) {
            Log.d("TodoViewModel", "Task deleted successfully!")
            loadTodoList() // Refresh UI after deletion
        } else {
            Log.w("TodoViewModel", "Failed to delete task")
        }
    }

    private fun fetchTasksFromSQLite(): List<TodoItem> {
        // Fetch tasks from SQLite database using the helper
        return databaseHelper.getAllTodoItems() ?: emptyList()
    }

    fun addTodoItem(todoItem: TodoItem) {
        // Add todo item to the database using the helper
        val newRowId = databaseHelper.insertTask(todoItem.todoName, todoItem.priority)
        if (newRowId != -1L) {
            Log.d("TodoViewModel", "Task inserted successfully!")
            loadTodoList() // Refresh UI after addition
        } else {
            Log.e("TodoViewModel", "Error inserting task!")
            // Handle failure if needed
        }
    }
}
