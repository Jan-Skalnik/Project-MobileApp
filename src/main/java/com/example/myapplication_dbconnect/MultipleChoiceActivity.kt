package com.example.myapplication_dbconnect

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MultipleChoiceActivity : AppCompatActivity() {
    private lateinit var questionText: TextView
    private lateinit var option1: CheckBox
    private lateinit var option2: CheckBox
    private lateinit var option3: CheckBox
    private lateinit var option4: CheckBox
    private lateinit var submitButton: Button

    private var correctAnswers = "0000" // Default value
    private var score = 0
    private var questionCount = 0
    private val maxQuestions = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_choice)

        questionText = findViewById(R.id.question_text)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)
        option4 = findViewById(R.id.option4)
        submitButton = findViewById(R.id.submit_button)

        loadNewQuestion()

        submitButton.setOnClickListener {
            checkAnswer()
        }
    }

    private fun loadNewQuestion() {
        if (questionCount >= maxQuestions) {
            endGame()
            return
        }
        FetchMCQuestion().execute()
    }

    private fun checkAnswer() {
        val userAnswers = "${if (option1.isChecked) '1' else '0'}" +
                "${if (option2.isChecked) '1' else '0'}" +
                "${if (option3.isChecked) '1' else '0'}" +
                "${if (option4.isChecked) '1' else '0'}"

        if (userAnswers == correctAnswers) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            questionCount++
            Handler().postDelayed({
                resetOptions()
                loadNewQuestion()
            }, 1000) // Delay before next question
        } else {
            Toast.makeText(this, "Wrong answer.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetOptions() {
        option1.isChecked = false
        option2.isChecked = false
        option3.isChecked = false
        option4.isChecked = false
    }

    private fun endGame() {
        questionText.text = "Game Over! Your final score: $score"
        submitButton.isEnabled = false

        Handler().postDelayed({
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    inner class FetchMCQuestion : AsyncTask<Void, Void, DatabaseConnection.QuestionMC?>() {
        override fun doInBackground(vararg params: Void?): DatabaseConnection.QuestionMC? {
            return DatabaseConnection.getRandomMCQuestion()
        }

        override fun onPostExecute(question: DatabaseConnection.QuestionMC?) {
            if (question != null) {
                questionText.text = question.text
                option1.text = question.option1
                option2.text = question.option2
                option3.text = question.option3
                option4.text = question.option4
                correctAnswers = question.correctAnswers
            } else {
                Toast.makeText(this@MultipleChoiceActivity, "Error loading question!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
