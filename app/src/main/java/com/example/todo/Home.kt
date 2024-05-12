package com.example.todo

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.todo_mobile_app.data.TodoDatabase
import com.example.todo_mobile_app.data.TodoItem
import com.example.todo_mobile_app.data.TodoItemDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Home : AppCompatActivity() {

    private lateinit var taskNameEditText: EditText
    private lateinit var taskDescriptionEditText: EditText
    private lateinit var prioritySpinner: Spinner
    private lateinit var addTaskButton: Button
    private lateinit var taskDao: TodoItemDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val imageView3 = findViewById<ImageView>(R.id.imageView3)

        imageView3.setOnClickListener {
            showPopupForm()
        }

        val db: TodoDatabase = Room.databaseBuilder(applicationContext, TodoDatabase::class.java, "tasks-db").build()
        taskDao = db.todoItemDao()
    }

    private fun showPopupForm() {
        try{
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.popup_layout_add, null)
        dialogBuilder.setView(dialogView)

        val dropdownValues = arrayOf("1", "2", "3")

        val spinner = dialogView.findViewById<Spinner>(R.id.priority_picker)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dropdownValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPriority = dropdownValues[position].toInt()
                // Do something with the selected priority
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Another interface callback
            }
        }

        val alertDialog = dialogBuilder.create()

        // Set the WindowManager LayoutParams to ensure the dialog appears correctly
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        taskNameEditText = dialogView.findViewById(R.id.task_name)
        taskDescriptionEditText = dialogView.findViewById(R.id.task_description)
        addTaskButton = dialogView.findViewById(R.id.add_task_button)

        addTaskButton.setOnClickListener {
            // Launch the save function in a coroutine scope
            CoroutineScope(Dispatchers.IO).launch {
                saveTaskToDatabase()
                // Dismiss the dialog after saving (consider using withContext(Dispatchers.Main) for UI updates)
                alertDialog.dismiss()
            }
        }

        alertDialog.show()

    } catch (e: Exception) {
        Log.e("Home", "Error in showPopupForm: ${e.message}", e)
    }
    }

    private suspend fun saveTaskToDatabase() {
        try{
        val taskName = taskNameEditText.text.toString().trim()
        val taskDescription = taskDescriptionEditText.text.toString().trim()
        val priority = prioritySpinner.selectedItem.toString().toInt()
        val task = TodoItem(todoName = taskName, description = taskDescription, priority = priority)

        taskDao.insertTodoItem(task) // This is a suspending function

    } catch (e: Exception) {
        Log.e("Home", "Error in saveTaskToDatabase: ${e.message}", e)
    }
    }
}
