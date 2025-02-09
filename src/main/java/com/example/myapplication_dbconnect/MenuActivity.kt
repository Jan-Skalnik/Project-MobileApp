
package com.example.myapplication_dbconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        Log.d("MenuActivity", "onCreate called")

        val textViewWelcome = findViewById<TextView>(R.id.textViewWelcome)
        val buttonGame1 = findViewById<Button>(R.id.buttonGame1)
        val buttonGame2 = findViewById<Button>(R.id.buttonGame2)



        // Retrieve user data from intent
        val userId = intent.getIntExtra("USER_ID", -1)
        val userName = intent.getStringExtra("USER_NAME")

        if (userId != -1 && !userName.isNullOrEmpty()) {
            textViewWelcome.text = "Welcome, $userName! (ID: $userId)"
            Log.d("MenuActivity", "User data received: ID=$userId, Name=$userName")
        } else {
            textViewWelcome.text = "Welcome!"
            Log.e("MenuActivity", "Failed to receive user data")
        }
        // Set button true or false click listener
        buttonGame1.setOnClickListener {
            val intent = Intent(this, TrueOrFalse::class.java)
            startActivity(intent)
        }
        buttonGame2.setOnClickListener {
            val intent = Intent(this, MultipleChoiceActivity::class.java)
            startActivity(intent)
        }
    }

}
