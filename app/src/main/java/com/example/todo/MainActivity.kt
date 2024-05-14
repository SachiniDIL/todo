package com.example.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check if username is already saved in shared preferences
        val sharedPref = getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", null)

        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)  // Delay for 3 seconds using coroutines
            withContext(Dispatchers.Main) {
                // Launch activity based on whether the username is set
                if (username.isNullOrEmpty()) {
                    // Username not set, launch login activity
                    val intent = Intent(this@MainActivity, login::class.java)
                    startActivity(intent)
                } else {
                    // Username is set, launch Home activity
                    val intent = Intent(this@MainActivity, Home::class.java)
                    startActivity(intent)
                }
                finish() // Finish current activity
            }
        }
    }
}
