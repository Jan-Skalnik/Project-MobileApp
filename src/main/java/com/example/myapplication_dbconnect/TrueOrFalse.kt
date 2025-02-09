package com.example.myapplication_dbconnect

import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class TrueOrFalse : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var buttonTrue: Button
    private lateinit var buttonFalse: Button
    private lateinit var buttonRestart: Button

    private var correctAnswer: Boolean = false
    private var score: Int = 0
    private var questionCount: Int = 0
    private val maxQuestions = 15

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trueorfalse)

        questionTextView = findViewById(R.id.textViewQuestion)
        scoreTextView = findViewById(R.id.textViewScore)
        buttonTrue = findViewById(R.id.buttonTrue)
        buttonFalse = findViewById(R.id.buttonFalse)
        buttonRestart = findViewById(R.id.buttonRestart)

        sharedPreferences = getSharedPreferences("TrueOrFalseGame", MODE_PRIVATE)

        // ✅ Resetujeme skóre a počet otázek při každém spuštění hry
        resetGame()

        buttonTrue.setOnClickListener { checkAnswer(true) }
        buttonFalse.setOnClickListener { checkAnswer(false) }
        buttonRestart.setOnClickListener { restartGame() }
    }

    private fun resetGame() {
        score = 0
        questionCount = 0
        saveProgress()
        updateScoreDisplay()
        buttonRestart.isEnabled = false
        buttonTrue.isEnabled = true
        buttonFalse.isEnabled = true
        FetchRandomQuestion().execute()
    }

    private fun checkAnswer(userAnswer: Boolean) {
        if (questionCount >= maxQuestions) return

        if (userAnswer == correctAnswer) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Wrong answer!", Toast.LENGTH_SHORT).show()
        }

        questionCount++
        saveProgress()
        updateScoreDisplay()

        if (questionCount >= maxQuestions) {
            endGame()
        } else {
            FetchRandomQuestion().execute()
        }
    }

    private fun saveProgress() {
        val editor = sharedPreferences.edit()
        editor.putInt("score", score)
        editor.putInt("questionCount", questionCount)
        editor.apply()
    }

    private fun updateScoreDisplay() {
        scoreTextView.text = "Score: $score | Question: $questionCount/$maxQuestions"
    }

    private fun endGame() {
        questionTextView.text = "Game Over! Your final score: $score"
        buttonTrue.isEnabled = false
        buttonFalse.isEnabled = false
        buttonRestart.isEnabled = true

        // ✅ Po 3 sekundách návrat do menu
        Handler().postDelayed({
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    private fun restartGame() {
        resetGame()
    }

    inner class FetchRandomQuestion : AsyncTask<Void, Void, Pair<String, Boolean>>() {
        override fun doInBackground(vararg params: Void?): Pair<String, Boolean> {
            var question = "Error loading question"
            var answer = false
            try {
                val connection: Connection = DriverManager.getConnection(
                    "jdbc:mariadb://ita03.vas-server.cz:3306/skalnik_Project",
                    "skalnik",
                    "AJ6q8hXwNhAOMDDY"
                )
                val query = "SELECT question_text, answer FROM questions ORDER BY RAND() LIMIT 1"
                val statement: PreparedStatement = connection.prepareStatement(query)
                val resultSet: ResultSet = statement.executeQuery()

                if (resultSet.next()) {
                    question = resultSet.getString("question_text")
                    answer = resultSet.getBoolean("answer")
                }

                resultSet.close()
                statement.close()
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return Pair(question, answer)
        }

        override fun onPostExecute(result: Pair<String, Boolean>) {
            questionTextView.text = result.first
            correctAnswer = result.second
        }
    }
}
