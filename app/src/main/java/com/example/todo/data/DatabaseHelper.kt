package com.example.todo.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion != DATABASE_VERSION) {
            //adding the new column
            db.execSQL("ALTER TABLE ${TodoContract.TodoEntry.TABLE_NAME} ADD COLUMN ${TodoContract.TodoEntry.COLUMN_IS_DONE} INTEGER DEFAULT 0")
        }
    }

    // Insert a new task into the database
    fun insertTask(taskName: String, priority: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TodoContract.TodoEntry.COLUMN_TASK_NAME, taskName)
            put(TodoContract.TodoEntry.COLUMN_PRIORITY, priority)
        }
        return db.insert(TodoContract.TodoEntry.TABLE_NAME, null, values)
    }

    // Update an existing task in the database
    fun updateTask(taskId: Int, taskName: String, priority: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TodoContract.TodoEntry.COLUMN_TASK_NAME, taskName)
            put(TodoContract.TodoEntry.COLUMN_PRIORITY, priority)
        }
        val selection = "${TodoContract.TodoEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(taskId.toString())
        return db.update(TodoContract.TodoEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    // Delete a task from the database
    fun deleteTask(taskId: Int): Int {
        val db = writableDatabase
        val selection = "${TodoContract.TodoEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(taskId.toString())
        return db.delete(TodoContract.TodoEntry.TABLE_NAME, selection, selectionArgs)
    }

    // Update the isDone status of a task
    fun updateIsDone(id: Int, done: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TodoContract.TodoEntry.COLUMN_IS_DONE, done)
        }
        val selection = "${TodoContract.TodoEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        return db.update(TodoContract.TodoEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    // Get all todo items from the database
    fun getAllTodoItems(): List<TodoItem> {
        val todoList = mutableListOf<TodoItem>()
        val db = readableDatabase
        val projection = arrayOf(
            TodoContract.TodoEntry.COLUMN_ID,
            TodoContract.TodoEntry.COLUMN_TASK_NAME,
            TodoContract.TodoEntry.COLUMN_PRIORITY,
            TodoContract.TodoEntry.COLUMN_IS_DONE
        )
        val cursor = db.query(
            TodoContract.TodoEntry.TABLE_NAME,  // The table to query
            projection, // The array of columns to return
            null,       // The columns for the WHERE clause
            null,       // The values for the WHERE clause
            null,       // don't group the rows
            null,       // don't filter by row groups
            null        // The sort order
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_ID))
                val taskName = getString(getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_TASK_NAME))
                val priority = getInt(getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_PRIORITY))
                val isDone = getInt(getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_IS_DONE))
                todoList.add(TodoItem(id, taskName, priority, isDone))
            }
        }
        cursor.close()
        return todoList
    }

    // Update the task status (isDone value)
    fun updateTaskStatus(id: Int, isDone: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TodoContract.TodoEntry.COLUMN_IS_DONE, isDone)
        }
        val selection = "${TodoContract.TodoEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        return db.update(TodoContract.TodoEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "Todo.db"
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${TodoContract.TodoEntry.TABLE_NAME} (" +
                    "${TodoContract.TodoEntry.COLUMN_ID} INTEGER PRIMARY KEY," +
                    "${TodoContract.TodoEntry.COLUMN_TASK_NAME} TEXT," +
                    "${TodoContract.TodoEntry.COLUMN_PRIORITY} INTEGER," +
                    "${TodoContract.TodoEntry.COLUMN_IS_DONE} INTEGER DEFAULT 0)"
    }
}
