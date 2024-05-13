package com.example.todo.data

import android.provider.BaseColumns

object TodoContract {
    // Table contents are grouped together in an anonymous object.
    object TodoEntry : BaseColumns {
        const val TABLE_NAME = "todo"
        const val COLUMN_ID = "id"
        const val COLUMN_TASK_NAME = "task_name"
        const val COLUMN_PRIORITY = "priority"
        const val COLUMN_IS_DONE = "is_done"
    }
}
