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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.data.TodoItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Home : AppCompatActivity() {
    // Firebase Database references
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("tasks")
        sharedPreferences = getSharedPreferences("todo_prefs", MODE_PRIVATE)


        val todoList = ArrayList<TodoItem>()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                for (snapshot in snapshot.children) {
                    val todo = snapshot.getValue(TodoItem::class.java)
                    todo?.let {
                        todoList.add(it)
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        val itemAdapter = TodoAdapter(todoList)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = itemAdapter

        val imageView3 = findViewById<ImageView>(R.id.imageView3)

        imageView3.setOnClickListener{
            showPopupForm() }

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

        val addTaskButton = dialogView.findViewById<Button>(R.id.add_task_button)
        addTaskButton.setOnClickListener {
            val taskName = dialogView.findViewById<EditText>(R.id.task_name).text.toString().trim()
            val selectedPriority = dialogView.findViewById<Spinner>(R.id.priority_picker).selectedItem.toString()
            val priority = Integer.parseInt(selectedPriority) // assuming priority is an integer

            val nextTodoId = sharedPreferences.getInt("todoId", 0) + 1


            // Create a Task object with retrieved data and incremented todoId
            val task = TodoItem(nextTodoId, taskName, priority)

            // Push task data to Firebase using push()
            val newTaskRef = databaseReference.push()
            newTaskRef.setValue(task)
                .addOnSuccessListener {
                    Log.d("Home", "Task added successfully!")
                    // Store the updated todoId for future use
                    sharedPreferences.edit().putInt("todoId", nextTodoId).apply()
                    // Dismiss the dialog after saving
                    alertDialog.dismiss()
                }
                .addOnFailureListener { exception ->
                    Log.w("Home", "Error adding task:", exception)
                }
        }

        alertDialog.show()
    }


//    private fun fetchDataFromFirebase() {
//        databaseReference.get().addOnSuccessListener { snapshot ->
//            todoList.clear()
//            for (dataSnapshot in snapshot.children) {
//                val taskName = dataSnapshot.child("todoName").getValue(String::class.java)
//                val priority = dataSnapshot.child("priority").getValue(Int::class.java)
//                val id = dataSnapshot.child("todoID").getValue((Int::class.java))
//                val status = dataSnapshot.child("status").getValue((String::class.java))
//
//                if (taskName != null && id != null && priority != null && status != null) {
//                    val todoItem = TodoItem(id, taskName, priority, status)
//                    todoList.add(todoItem)
//                }
//            }
//            itemAdapter.notifyDataSetChanged() // Notify adapter of data change
//        }
//    }

}

class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val taskNameTextView: TextView = itemView.findViewById(R.id.textView) // Replace with your IDs
    // ... other view elements from todo_item.xml
}
