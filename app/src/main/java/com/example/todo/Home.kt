package com.example.todo

import android.app.AlertDialog
import android.content.SharedPreferences
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.data.DatabaseHelper
import com.example.todo.data.TodoAdapter
import com.example.todo.data.TodoContract
import com.example.todo.data.TodoItem
import com.example.todo.data.TodoViewModel
import com.example.todo.data.TodoViewModelFactory
import kotlinx.coroutines.launch

class Home : AppCompatActivity(), TodoAdapter.OnItemClickListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var todoViewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("todo_prefs", MODE_PRIVATE)

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        todoAdapter = TodoAdapter(mutableListOf(), this, DatabaseHelper(this))
        recyclerView.adapter = todoAdapter


        // Setup ItemTouchHelper for swipe-to-delete functionality
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Not needed for swipe-to-delete
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val itemToDelete = todoAdapter.getItemAtPosition(position)

                // Delete the item from SQLite database
                deleteTaskFromDatabase(itemToDelete.id)

                // Remove the item from the RecyclerView
                todoAdapter.removeItem(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Setup click listener for the "Add Task" button
        val imageView3 = findViewById<ImageView>(R.id.imageView3)
        imageView3.setOnClickListener {
            showPopupForm()
        }

        // Create ViewModel instance with the required factory
        val viewModelFactory = TodoViewModelFactory(DatabaseHelper(this))
        todoViewModel = ViewModelProvider(this, viewModelFactory).get(TodoViewModel::class.java)

        // Load initial data from SQLite database
        lifecycleScope.launch {
            try {
                val todoList = fetchTasksFromSQLite()
                updateUI(todoList)
            } catch (e: Exception) {
                // Handle exceptions
                Log.e("Home", "Error fetching tasks:", e)
            }
        }
    }

    private fun fetchTasksFromSQLite(): List<TodoItem> {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            TodoContract.TodoEntry.COLUMN_ID,
            TodoContract.TodoEntry.COLUMN_TASK_NAME,
            TodoContract.TodoEntry.COLUMN_PRIORITY
        )

        val cursor = db.query(
            TodoContract.TodoEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val tasks = mutableListOf<TodoItem>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_ID))
                val taskName = getString(getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_TASK_NAME))
                val priority = getInt(getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_PRIORITY))
                tasks.add(TodoItem(id, taskName, priority, 0))
            }
        }
        cursor.close()
        return tasks
    }

    private fun updateUI(todoList: List<TodoItem>) {
        todoAdapter.updateItems(todoList)
    }

    private fun deleteTaskFromDatabase(taskId: Int) {
        val dbHelper = DatabaseHelper(this)
        val deletedRows = dbHelper.deleteTask(taskId)
        if (deletedRows > 0) {
            Log.d("Home", "Task deleted successfully!")
        } else {
            Log.w("Home", "Failed to delete task")
        }
    }

    private fun showPopupForm() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.popup_layout_add, null)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)

        val dropdownValues = arrayOf("1", "2", "3")

        val spinner = dialogView.findViewById<Spinner>(R.id.priority_picker)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dropdownValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle item selection if needed
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }

        val alertDialog = dialogBuilder.create()

        // Set the WindowManager LayoutParams to ensure the dialog appears correctly
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val addTaskButton = dialogView.findViewById<Button>(R.id.add_task_button)
        addTaskButton.setOnClickListener {
            val taskName = dialogView.findViewById<EditText>(R.id.task_name).text.toString().trim()
            val selectedPriority = spinner.selectedItem.toString()
            val priority = Integer.parseInt(selectedPriority)

            // Insert the task into the database
            val dbHelper = DatabaseHelper(this)
            val newRowId = dbHelper.insertTask(taskName, priority)

            if (newRowId != -1L) {
                Log.d("Home", "Task inserted successfully!")
                // Refresh UI if needed
                val newTask = TodoItem(newRowId.toInt(), taskName, priority, 0)
                todoAdapter.addItem(newTask) // Add new task to RecyclerView
                alertDialog.dismiss()
            } else {
                Log.e("Home", "Error inserting task!")
                // Handle failure if needed
            }
        }

        alertDialog.show()
    }

    override fun onItemClick(position: Int) {
        // Retrieve the clicked item from the adapter
        val clickedItem = todoAdapter.getItemAtPosition(position)

        // Show a toast message with the name of the clicked item
        Toast.makeText(this, "Clicked item: ${clickedItem.todoName}", Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateClick(position: Int) {
        showUpdatePopup(todoAdapter.getItemAtPosition(position))
    }

    private fun showUpdatePopup(todoItem: TodoItem) {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.popup_layout_update, null)

        // Populate input fields with relevant item data
        dialogView.findViewById<EditText>(R.id.task_name).setText(todoItem.todoName)
        dialogView.findViewById<Spinner>(R.id.priority_picker).setSelection(todoItem.priority - 1)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()

        // Set the WindowManager LayoutParams to ensure the dialog appears correctly
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val updateTaskButton = dialogView.findViewById<Button>(R.id.add_task_button)
        updateTaskButton.setOnClickListener {
            val updatedTaskName = dialogView.findViewById<EditText>(R.id.task_name).text.toString().trim()
            val updatedPriority = dialogView.findViewById<Spinner>(R.id.priority_picker).selectedItem.toString().toInt()

            // Update the task in the database
            val dbHelper = DatabaseHelper(this)
            val rowsAffected = dbHelper.updateTask(todoItem.id, updatedTaskName, updatedPriority)

            if (rowsAffected > 0) {
                Log.d("Home", "Task updated successfully!")
                // Refresh UI if needed
                todoItem.todoName = updatedTaskName
                todoItem.priority = updatedPriority
                todoAdapter.notifyDataSetChanged()
                alertDialog.dismiss()
            } else {
                Log.e("Home", "Error updating task!")
                // Handle failure if needed
            }
        }

        alertDialog.show()
    }

}
