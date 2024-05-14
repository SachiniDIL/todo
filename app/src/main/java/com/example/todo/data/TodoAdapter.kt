package com.example.todo.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R

class TodoAdapter(
    private var todoList: MutableList<TodoItem>,
    private val listener: OnItemClickListener,
    private val databaseHelper: DatabaseHelper
) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val todoNameTextView: TextView = itemView.findViewById(R.id.textView)
        val todoCheckBox: ImageView = itemView.findViewById(R.id.imageView4)
        private val updateButton: ImageView = itemView.findViewById(R.id.imageView5)

        init {
            // Set click listener on the item view for general clicks
            itemView.setOnClickListener(this)

            // Set click listener for the update button
            updateButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onUpdateClick(position)
                }
            }

            // Set click listener for the checkbox
            todoCheckBox.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentItem = todoList[position]
                    val newIsDoneValue = if (currentItem.isDone == 1) 0 else 1
                    currentItem.isDone = newIsDoneValue
                    databaseHelper.updateIsDone(currentItem.id, newIsDoneValue)
                    notifyItemChanged(position)
                }
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onUpdateClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = todoList[position]
        holder.todoNameTextView.text = currentItem.todoName

        // Set the image resource based on the todoItem status
        if (currentItem.isDone == 1) {
            holder.todoCheckBox.setImageResource(R.drawable.baseline_check_circle_outline_24)
            holder.todoNameTextView.alpha = 0.5f // Make the item text blurred
        } else {
            holder.todoCheckBox.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            holder.todoNameTextView.alpha = 1f // Reset the item text alpha
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun updateItems(newTodoList: List<TodoItem>) {
        todoList.clear()
        todoList.addAll(newTodoList)
        notifyDataSetChanged()
    }

    fun getItemAtPosition(position: Int): TodoItem {
        return todoList[position]
    }

    fun removeItem(position: Int) {
        todoList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(newTask: TodoItem) {
        // Add the new task to the list
        todoList.add(newTask)
        // Notify adapter of the insertion
        notifyItemInserted(todoList.size - 1)
    }
}
