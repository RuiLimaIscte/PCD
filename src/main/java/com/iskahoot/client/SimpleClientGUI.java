package com.iskahoot.client;

import com.iskahoot.common.messages.CurrentQuestion;
import com.iskahoot.common.models.Question;
import com.iskahoot.common.models.Quiz;
import com.iskahoot.utils.AnswerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import static com.iskahoot.utils.QuestionLoader.loadFromFile;

public class SimpleClientGUI extends JFrame {

    private JLabel questionLabel;
    private JButton[] optionButtons;
    private static JLabel timerLabel;
    private JTextArea scoreboardArea;

    private CurrentQuestion currentQuestion;
    private AnswerListener listener;

    private List<Question> questions;
    private int currentQuestionindex = 0;

    public int getSelectedOption() {
        return selectedOption;
    }

    private int selectedOption = -1;
   // private boolean waitingForAnswer = false;


    private String clientInfo;


//    public static void main(String[] args) throws IOException {
//        Quiz quiz = new Quiz(loadFromFile("src/main/resources/questions.json").getName(),
//                loadFromFile("src/main/resources/questions.json").getQuestions());
//
//        new SimpleClientGUI(quiz.getQuestions(), "Client 1");
//    }

    public SimpleClientGUI(CurrentQuestion currentQuestion, String clientInfo, AnswerListener listener) {

        this.currentQuestion = currentQuestion;
        this.clientInfo = clientInfo;
        this.listener = listener;

        setTitle(clientInfo);
        setSize(700, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildGUI();

        displayQuestion(currentQuestion);
//        if (questions != null && !questions.isEmpty()) {
//            displayQuestion(questions.get(0));
//        }

        setVisible(true);
    }

    private void buildGUI() {

        // pergunta
        questionLabel = new JLabel("Pergunta", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(questionLabel, BorderLayout.NORTH);

        //opçoes
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        optionButtons = new JButton[4];

        for (int i = 0; i < 4; i++) {
            int optionIndex = i;
            optionButtons[i] = new JButton("Opção " + (i + 1));
            optionButtons[i].addActionListener(e -> handleOption(optionIndex));
            optionsPanel.add(optionButtons[i]);
        }

        add(optionsPanel, BorderLayout.CENTER);

        // side panel
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(180, 0));

        // Label Kahoot
        JLabel kahootLabel = new JLabel("Kahoot", SwingConstants.CENTER);
        sidePanel.add(kahootLabel, BorderLayout.NORTH);

        // Scoreboard
        scoreboardArea = new JTextArea();
        scoreboardArea.setEditable(false);
        scoreboardArea.setText("Waiting for scoreboard...");
        sidePanel.add(new JScrollPane(scoreboardArea), BorderLayout.CENTER);

        // Timer
        timerLabel = new JLabel("Timer: 30", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        sidePanel.add(timerLabel, BorderLayout.SOUTH);

        add(sidePanel, BorderLayout.EAST);
    }

    private void displayQuestion(CurrentQuestion p) {
        questionLabel.setText(p.getQuestionText());
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(p.getOptions().get(i));
        }
    }


//    private void displayQuestion(Question q) {
//
////        waitingForAnswer = true;
//        selectedOption = -1;
//
//        questionLabel.setText(q.getQuestion());
//
//        for (int i = 0; i < 4; i++) {
//            optionButtons[i].setText(q.getOptions().get(i));
//            optionButtons[i].setEnabled(true);
//        }
//        //        startTimer(30);
//        //   updateTimerLabel(30);
//    }

    //Timer update method
    public static void updateTimerLabel(long timeLeft) {
//        if (timeLeft < 0) timeLeft = 0;
//        timerLabel.setText("Timer: " + timeLeft);
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText("Timer: " + timeLeft);
        });
    }



    private void handleOption(int index) {

        selectedOption = index;
//        waitingForAnswer = false;
//        stopTimer();
        //TODO enviar resposta para o servidor
//        sendAnswer();

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setEnabled(false);
            if (i == index) {
                optionButtons[i].setBackground(Color.GREEN);
            }
        }
        if (currentQuestion != null && listener != null) {
            // 1. O Cliente PREENCHE o campo vazio
            currentQuestion.setSelectedAnswerIndex(index);
            // 2. Envia o objeto modificado de volta via Callback
            listener.onAnswerSelected(currentQuestion);
        }
        System.out.println("Selected: " + index);
    }


//    private void nextQuestion() {
//
//        currentQuestion++;
//
//        if (currentQuestion >= questions.size()) {
//            showGameEnded();
//            return;
//        }
//
//        // Reset button color
//        for (JButton b : optionButtons)
//            b.setBackground(null);
//
//        displayQuestion(questions.get(currentQuestion));
//    }

    private void showGameEnded() {

//        stopTimer();

        questionLabel.setText("GAME OVER");
        for (JButton b : optionButtons) {
            b.setEnabled(false);
            b.setVisible(false);
        }

        timerLabel.setText("Timer: --");

        JOptionPane.showMessageDialog(this, "O jogo terminou!");

        dispose();

    }
}
