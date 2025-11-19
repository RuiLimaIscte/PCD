package com.iskahoot.client;

import com.iskahoot.common.models.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import static com.iskahoot.utils.QuestionLoader.loadFromFile;

public class SimpleClientGUI extends JFrame {

    private JLabel questionLabel;
    private JButton[] optionButtons;
    private JLabel timerLabel;
    private JTextArea scoreboardArea;

    private List<Question> questions;
    private int currentQuestion = 0;

    private int selectedOption = -1;
    private boolean waitingForAnswer = false;

    private Timer swingTimer;
    private int timeLeft;

    private String clientInfo;


    public static void main(String[] args) throws IOException {
        new SimpleClientGUI(loadFromFile("src/main/resources/questions.json"), "Player1");
    }

    public SimpleClientGUI(List<Question> questions, String clientInfo) {

        this.questions = questions;
        this.clientInfo = clientInfo;

        setTitle(clientInfo);
        setSize(700, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildGUI();

        if (questions != null && !questions.isEmpty()) {
            displayQuestion(questions.get(0));
        }

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

        // --- PAINEL LATERAL ---
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

    // -----------------------------------------
    //         DISPLAY QUESTION
    // -----------------------------------------
    private void displayQuestion(Question q) {

        waitingForAnswer = true;
        selectedOption = -1;

        questionLabel.setText(q.getQuestion());

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(q.getOptions().get(i));
            optionButtons[i].setEnabled(true);
        }

        startTimer(30);
    }

    // -----------------------------------------
    //              TIMER
    // -----------------------------------------
    private void startTimer(int seconds) {

        if (swingTimer != null) {
            swingTimer.stop();
        }

        timeLeft = seconds;
        timerLabel.setText("Timer: " + timeLeft);

        swingTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Timer: " + timeLeft);

            if (timeLeft <= 0) {
                swingTimer.stop();
                timesUp();
            }
        });

        swingTimer.start();
    }

    private void stopTimer() {
        if (swingTimer != null) swingTimer.stop();
    }

    private void timesUp() {
        waitingForAnswer = false;

        for (JButton b : optionButtons)
            b.setEnabled(false);

        JOptionPane.showMessageDialog(this, "Tempo esgotado!");
        nextQuestion();
    }

    // -----------------------------------------
    //        HANDLE OPTION SELECTION
    // -----------------------------------------
    private void handleOption(int index) {

        if (!waitingForAnswer) return;

        selectedOption = index;
        waitingForAnswer = false;
        stopTimer();

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setEnabled(false);
            if (i == index) {
                optionButtons[i].setBackground(Color.YELLOW);
            }
        }

        System.out.println("Selected: " + index);

        nextQuestion();
    }

    // -----------------------------------------
    //          NEXT QUESTION
    // -----------------------------------------
    private void nextQuestion() {

        currentQuestion++;

        if (currentQuestion >= questions.size()) {
            showGameEnded();
            return;
        }

        // Reset button color
        for (JButton b : optionButtons)
            b.setBackground(null);

        displayQuestion(questions.get(currentQuestion));
    }

    // -----------------------------------------
    //         GAME ENDED SCREEN
    // -----------------------------------------
    private void showGameEnded() {

        stopTimer();

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
