// login.kt
package com.example.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val continueButton: Button = findViewById(R.id.continueButton)

        continueButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            if (username.isNotEmpty()) {
                saveUsername(username)
                launchHomeActivity()
            } else {
                usernameEditText.error = "Please enter your username"
            }
        }
    }

    private fun saveUsername(username: String) {
        val sharedPref = getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)?:
        return
        with(sharedPref.edit()) {
            putString("username", username)
            apply()
        }
    }

    private fun launchHomeActivity() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }
}
