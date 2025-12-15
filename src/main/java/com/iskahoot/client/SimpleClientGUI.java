package com.iskahoot.client;

import com.iskahoot.common.messages.QuestionMessage;
import com.iskahoot.common.messages.ScoreboardData;
import com.iskahoot.common.models.Player;
import com.iskahoot.utils.AnswerListener;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SimpleClientGUI extends JFrame {

    private JLabel questionLabel;
    private JButton[] optionButtons;
    private static JLabel timerLabel;
    private JTextArea scoreboardArea;

    private Thread countdownThread;

    private QuestionMessage questionMessage;
    private AnswerListener listener;

    private Player player;


    public SimpleClientGUI(QuestionMessage questionMessage, Player clientInfo, AnswerListener listener) {

        this.questionMessage = questionMessage;
        this.player = clientInfo;
        this.listener = listener;

        setTitle("Kahoot Client: " + player.getPlayerCode() + " " + player.getTeamCode() + " " + player.getGameCode() + " " + questionMessage.getQuestionType() );
        setSize(700, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildGUI();

        displayQuestion(questionMessage);

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

        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(180, 0));

        JLabel kahootLabel = new JLabel("Kahoot", SwingConstants.CENTER);
        sidePanel.add(kahootLabel, BorderLayout.NORTH);

        scoreboardArea = new JTextArea();
        scoreboardArea.setEditable(false);
        scoreboardArea.setText("Waiting for scoreboard");
        sidePanel.add(new JScrollPane(scoreboardArea), BorderLayout.CENTER);

        // Timer
        timerLabel = new JLabel("Timer - 30", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        sidePanel.add(timerLabel, BorderLayout.SOUTH);

        add(sidePanel, BorderLayout.EAST);
    }

    private void displayQuestion(QuestionMessage questionMessage) {
        questionLabel.setText(questionMessage.getQuestionText());
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(questionMessage.getOptions().get(i));
        }
    }

    public void updateScoreboard(ScoreboardData data) {
        StringBuilder sb = new StringBuilder();

        sb.append(" RONDA ").append(data.getCurrentRound())
                .append("/").append(data.getTotalRounds()).append(" \n\n");

        // Iterar pelas equipas e ordenandar por pontuação total
        data.getTeamScores().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    String team = entry.getKey();
                    int totalScore = entry.getValue();
                    int lastRoundScore = data.getLastRoundScores().getOrDefault(team, 0);

                    sb.append(team).append("\n")
                            .append("   Score: ").append(totalScore)
                            .append(" | LastRound: ").append(lastRoundScore)
                            .append("\n\n");
                });

        scoreboardArea.setText(sb.toString());

        if (data.getCurrentRound() == data.getTotalRounds()) {
            showGameEndedPopUp();
        }
    }

    public void updateQuestion(QuestionMessage newQuestionMessage) {
        this.questionMessage = newQuestionMessage;

        questionLabel.setText(newQuestionMessage.getQuestionText());

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(newQuestionMessage.getOptions().get(i));
            optionButtons[i].setBackground(null);
            optionButtons[i].setEnabled(true);
        }

        timerLabel.setText("Timer - 30");
        setTitle("Kahoot Client: " + player.getPlayerCode() + " " + player.getTeamCode() + " " + player.getGameCode() + " " + questionMessage.getQuestionType() );
    }

    public void startTimer(int totalSeconds) {
        countdownThread = new Thread(() -> {
            int timeLeft = totalSeconds;
            try {
                while (timeLeft >= 0) {
                    timerLabel.setText("Timer - " + timeLeft);
                    if (timeLeft <= 5) {
                        timerLabel.setForeground(Color.RED);
                    } else {
                        timerLabel.setForeground(Color.BLACK);
                    }
                    Thread.sleep(1000);
                    timeLeft--;
                }
                timerLabel.setText("Tempo Acabou");
            } catch (InterruptedException e) {
            }
        });
        countdownThread.start();
    }

    public void stopTimer() {
        if (countdownThread != null && countdownThread.isAlive()) {
            countdownThread.interrupt();
        }
    }

    private void handleOption(int index) {
        stopTimer();

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setEnabled(false);
            if (i == index) {
                optionButtons[i].setBackground(Color.GREEN);
            }
        }
        if (questionMessage != null && listener != null) {
            questionMessage.setSelectedAnswerIndex(index);
            questionMessage.setClientCode(player.getPlayerCode());
            listener.onAnswerSelected(questionMessage);
        }
        //System.out.println("Selected: " + index);
    }

    private void showGameEndedPopUp() {
        stopTimer();

        JDialog results = new JDialog(this, "Resultados Finais", true);
        results.setSize(400, 300);
        results.setLocationRelativeTo(this);

        JTextArea finalScores = new JTextArea();
        finalScores.setText(scoreboardArea.getText());
        finalScores.setEditable(false);

        results.add(new JScrollPane(finalScores));

        JButton closeButton = new JButton("Fechar");
        closeButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeButton);
        results.add(bottomPanel, BorderLayout.SOUTH);

        results.setVisible(true);
    }

}
