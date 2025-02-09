package com.example.myapplication_dbconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

class MainActivity : AppCompatActivity() {

    companion object {
        const val DB_URL = "jdbc:mariadb://ita03.vas-server.cz:3306/skalnik_Project"
        const val DB_USER = "skalnik"
        const val DB_PASSWORD = "AJ6q8hXwNhAOMDDY"
    }

    private lateinit var textViewId: TextView
    private lateinit var textViewName: TextView
    private lateinit var buttonLoad: Button
    private lateinit var buttonSignUp: Button
    private lateinit var buttonLogin: Button
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        textViewId = findViewById(R.id.textViewId)
        textViewName = findViewById(R.id.textViewName)
        buttonLoad = findViewById(R.id.buttonLoad)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        buttonLogin = findViewById(R.id.buttonLogin)
        editTextUsername = findViewById(R.id.editTextUsername)
        //editTextPassword = findViewById(R.id.editTextPassword)

        // Button click listeners
        buttonLoad.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                fetchUserData()
            }
        }

        buttonSignUp.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                signUpUser()
            }
        }

        buttonLogin.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                loginUser()
            }
        }
    }

    private suspend fun fetchUserData() {
        try {
            Class.forName("org.mariadb.jdbc.Driver")
            val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
            val query = "SELECT * FROM Users LIMIT 1"
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val id = resultSet.getInt("id")
                val name = resultSet.getString("name")

                withContext(Dispatchers.Main) {
                    textViewId.text = "ID: $id"
                    textViewName.text = "Name: $name"
                }
            }

            resultSet.close()
            statement.close()
            connection.close()

        } catch (e: Exception) {
            Log.e("Database", "Error fetching data: ${e.message}")
        }
    }

    private suspend fun signUpUser() {
        try {
            val name = editTextUsername.text.toString()

            if (name.isNotEmpty()) {
                Class.forName("org.mariadb.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
                val query = "INSERT INTO Users (name) VALUES (?)"
                val statement = connection.prepareStatement(query)
                statement.setString(1, name)

                val rowsAffected = statement.executeUpdate() // Execute only once

                if (rowsAffected > 0) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error during sign-up", Toast.LENGTH_SHORT).show()
                    }
                }

                statement.close()
                connection.close()
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Please enter a name", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("Database", "Error signing up: ${e.message}")
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Error during sign-up", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private suspend fun loginUser() {
        try {
            val name = editTextUsername.text.toString()

            if (name.isNotEmpty()) {
                Class.forName("org.mariadb.jdbc.Driver")
                val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
                val query = "SELECT * FROM Users WHERE name = ?"
                val statement = connection.prepareStatement(query)
                statement.setString(1, name)
                val resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val id = resultSet.getInt("id")
                    val username = resultSet.getString("name")

                    withContext(Dispatchers.Main) {
                        Log.d("Login", "Login successful for User ID: $id, Username: $username")
                        Toast.makeText(this@MainActivity, "Login successful!", Toast.LENGTH_SHORT).show()

                        // Navigate to MenuActivity
                        val intent = Intent(this@MainActivity, MenuActivity::class.java)
                        intent.putExtra("USER_ID", id)
                        intent.putExtra("USER_NAME", username)
                        startActivity(intent)
                        finish() // Optional: Close MainActivity

                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }

                resultSet.close()
                statement.close()
                connection.close()
            }
        } catch (e: Exception) {
            Log.e("Database", "Error logging in: ${e.message}")
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "An error occurred during login", Toast.LENGTH_SHORT).show()
            }

        }


    }

}
