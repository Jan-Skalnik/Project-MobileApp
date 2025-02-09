package com.example.myapplication_dbconnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mariadb://ita03.vas-server.cz:3306/skalnik_Project";
    private static final String DB_USER = "skalnik";
    private static final String DB_PASSWORD = "AJ6q8hXwNhAOMDDY";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Metoda pro načtení náhodné otázky True/False
    public static Question getRandomQuestion() {
        Question question = null;
        String query = "SELECT question_text, answer FROM questions ORDER BY RAND() LIMIT 1";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                String text = resultSet.getString("question_text");
                boolean answer = resultSet.getBoolean("answer");
                question = new Question(text, answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return question;
    }

    // Metoda pro načtení náhodné otázky Multiple Choice
    public static QuestionMC getRandomMCQuestion() {
        QuestionMC question = null;
        String query = "SELECT question_text, option1, option2, option3, option4, correct_answers FROM questions_mc ORDER BY RAND() LIMIT 1";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                String text = resultSet.getString("question_text");
                String option1 = resultSet.getString("option1");
                String option2 = resultSet.getString("option2");
                String option3 = resultSet.getString("option3");
                String option4 = resultSet.getString("option4");
                String correctAnswers = resultSet.getString("correct_answers"); // Správný název sloupce

                question = new QuestionMC(text, option1, option2, option3, option4, correctAnswers);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return question;
    }


    // Třída pro True/False otázky
    public static class Question {
        String text;
        boolean answer;

        public Question(String text, boolean answer) {
            this.text = text;
            this.answer = answer;
        }
    }

    // Třída pro Multiple Choice otázky
    public static class QuestionMC {
        String text;
        String option1, option2, option3, option4;
        String correctAnswers; // String jako "1010"

        public QuestionMC(String text, String option1, String option2, String option3, String option4, String correctAnswers) {
            this.text = text;
            this.option1 = option1;
            this.option2 = option2;
            this.option3 = option3;
            this.option4 = option4;
            this.correctAnswers = correctAnswers;
        }
    }
}
