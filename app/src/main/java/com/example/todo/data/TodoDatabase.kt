package com.example.todo_mobile_app.data
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todo_mobile_app.data.TodoItem
import com.example.todo_mobile_app.data.TodoItemDao
import com.example.todo_mobile_app.data.User
import com.example.todo_mobile_app.data.UserDao

@Database(entities = [TodoItem::class, User::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoItemDao(): TodoItemDao
    abstract fun userDao(): UserDao  // Add this line


    companion object {
        private const val DATABASE_NAME = "todo-db"

        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getDatabase(context: android.content.Context): TodoDatabase {
            if (INSTANCE == null) {
                synchronized(TodoDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext, TodoDatabase::class.java, DATABASE_NAME)
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}