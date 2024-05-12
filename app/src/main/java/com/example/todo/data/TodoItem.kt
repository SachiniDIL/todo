package com.example.todo_mobile_app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val todoID: Long = 0,
    @ColumnInfo(name = "todoName") val todoName: String?,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "time") val time: Long? = null,
    @ColumnInfo(name = "date") val date: Long? = null,
    @ColumnInfo(name = "priority") val priority: Int = 0
)
