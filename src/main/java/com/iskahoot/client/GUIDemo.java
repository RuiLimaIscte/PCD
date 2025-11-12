package com.iskahoot.client;

import com.iskahoot.common.models.Question;
import com.iskahoot.common.models.Quiz;
import com.iskahoot.utils.QuestionLoader;

import javax.swing.*;
import java.util.List;

public class GUIDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Quiz quiz = QuestionLoader.loadFromResources("questions.json");
                List<Question> questions = quiz.getQuestions();

                ClientGUI gui = new ClientGUI(questions);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error loading questions: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

