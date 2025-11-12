package com.iskahoot.client;

import com.iskahoot.common.models.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * GUI for the client application
 * TODO: Implement in Phase 1
 */
public class ClientGUI extends JFrame {
    //private Client client;

    public interface AnswerCallback {
        void onAnswerSelected(int questionIndex, int answerIndex);
    }

    public interface NextQuestionCallback {
        void onNextQuestion(int newQuestionIndex);
    }

    private JLabel questionLabel;
    private JButton[] optionButtons;
    private JLabel timerLabel;
    private JTextArea scoreboardArea;
    private JPanel mainPanel, timerPanel,optionsPanel,questionPanel;

    private int BEGINTIME=30;
    private int currentTime;
    private Thread timerThread;
    private volatile boolean timerRunning;

    private int selectedOption = -1;
    private boolean waitingForAnswer = false;

    private List<Question> questions;
    private int currentQuestionIndex = 0;

    private AnswerCallback answerCallback;
    private NextQuestionCallback nextQuestionCallback;

//    public ClientGUI(Client client) {
//        this.client = client;
//        initializeGUI();
//    }

    public ClientGUI() {
        initializeGUI();
    }

    public ClientGUI(List<Question> questions) {
        this.questions = questions;
        currentQuestionIndex = 0;
        initializeGUI();

        if (questions != null && !questions.isEmpty()) {
            showNextQuestion();
        } else {
            questionLabel.setText("Sem perguntas carregadas!");
        }
    }

    public void setAnswerCallback(AnswerCallback callback) {
        this.answerCallback = callback;
    }

    public void setNextQuestionCallback(NextQuestionCallback callback) {
        this.nextQuestionCallback = callback;
    }
    // Stub so para testar a implementação do scoreboard temporario
    public void updateScoreboard(com.iskahoot.common.messages.ScoreboardData scoreboard) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════\n");
        sb.append("    ROUND ").append(scoreboard.getCurrentRound())
          .append("/").append(scoreboard.getTotalRounds()).append("\n");
        sb.append("═══════════════════════════\n\n");

        sb.append("🏆 TEAM STANDINGS:\n");
        sb.append("───────────────────────────\n");

        scoreboard.getTeamScores().forEach((team, score) -> {
            sb.append(String.format("%-10s %4d pts\n", team, score));
        });

        displayScoreboard(sb.toString());
    }
    // Stub so para testar a implementação do scoreboard temporario
    public void updateInitialScoreboard(int totalQuestions) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════\n");
        sb.append("    ROUND 1/").append(totalQuestions).append("\n");
        sb.append("═══════════════════════════\n\n");

        sb.append("🏆 TEAM STANDINGS:\n");
        sb.append("───────────────────────────\n");
        sb.append("TEAM1        0 pts\n");

        displayScoreboard(sb.toString());
    }

    public void showNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            displayQuestion(q.getQuestion(),
                    q.getOptions().toArray(new String[0]), 30);
        } else {
            showGameEnded("Jogo concluído!");
        }
    }

    /**
     * Avança para a próxima questão
     */
    public void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            if (nextQuestionCallback != null) {
                nextQuestionCallback.onNextQuestion(currentQuestionIndex);
            }
            resetForNextQuestion();
            showNextQuestion();
        } else {
            showGameEnded("Jogo concluído!");
        }
    }

    private void initializeGUI() {
        setTitle("IsKahoot - Quiz Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(46, 49, 146));


        timerPanel = displayTimerPanel();
        mainPanel.add(timerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);


        questionPanel = createQuestionPanel();
        centerPanel.add(questionPanel, BorderLayout.NORTH);

        optionsPanel = createOptionsPanel();
        centerPanel.add(optionsPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel scoreboardPanel = createScoreboardPanel();
        mainPanel.add(scoreboardPanel, BorderLayout.EAST);

        JButton nextButton = new JButton("Próxima Pergunta");
        nextButton.setFont(new Font("Arial", Font.BOLD, 18));
        nextButton.setBackground(new Color(46, 49, 146));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        nextButton.addActionListener(e -> nextQuestion());

        mainPanel.add(nextButton, BorderLayout.SOUTH);


        add(mainPanel);
        setVisible(true);
    }

    private JPanel displayTimerPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        timerLabel = new JLabel(String.valueOf(BEGINTIME));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 48));
        timerLabel.setForeground(Color.WHITE);

        panel.add(timerLabel);
        return panel;

    }

    public void updateTimer(int secondsRemaining) {
        timerLabel.setText(String.valueOf(secondsRemaining));

        // Change color based on time remaining
        if (secondsRemaining <= 5) {
            timerLabel.setForeground(new Color(230, 46, 73)); // Red
        } else if (secondsRemaining <= 10) {
            timerLabel.setForeground(new Color(255, 161, 23)); // Orange
        } else {
            timerLabel.setForeground(Color.WHITE);
        }
    }

    /**
     * Start the countdown timer
     */
    public void startTimer(int seconds) {
        // Stop any existing timer
        stopTimer();

        currentTime = seconds;
        timerRunning = true;

        timerThread = new Thread(() -> {
            while (timerRunning && currentTime > 0) {
                updateTimer(currentTime);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                currentTime--;
            }
            if (currentTime <= 0) {
                updateTimer(0);
                // Time expired
                System.out.println("Time's up!");
                stopTimer();

            }
        });
        timerThread.start();
    }

    /**
     * Stop the countdown timer
     */
    public void stopTimer() {
        timerRunning = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }
    }

    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        questionLabel = new JLabel("Waiting for question...", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 24));
        questionLabel.setForeground(new Color(46, 49, 146));

        panel.add(questionLabel, BorderLayout.CENTER);
        return panel;
    }


    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Colors for Kahoot-style buttons
        Color[] colors = {
                new Color(230, 46, 73),   // Red
                new Color(19, 104, 206),  // Blue
                new Color(255, 161, 23),  // Orange
                new Color(38, 137, 12)    // Green
        };

        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            final int optionIndex = i;
            optionButtons[i] = new JButton();
            optionButtons[i].setFont(new Font("Arial", Font.BOLD, 18));
            optionButtons[i].setForeground(Color.WHITE);
            optionButtons[i].setBackground(colors[i]);
            optionButtons[i].setFocusPainted(false);
            optionButtons[i].setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            optionButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            optionButtons[i].setEnabled(false);

            optionButtons[i].addActionListener(e -> {
                if (waitingForAnswer) {
                    selectOption(optionIndex);
                }
            });

            panel.add(optionButtons[i]);
        }

        return panel;
    }


    public void displayQuestion(String questionText, String[] options, int timeLimit) {
        // garantir execução na Event Dispatch Thread (conceito fundamental de Swing)
        SwingUtilities.invokeLater(() -> {
            // Atualizar o texto da pergunta
            questionLabel.setText(questionText);

            // Atualizar as opções
            for (int i = 0; i < optionButtons.length; i++) {
                if (i < options.length) {
                    optionButtons[i].setText(options[i]);
                    optionButtons[i].setEnabled(true);
                    optionButtons[i].setVisible(true);
                } else {
                    optionButtons[i].setVisible(false);
                }

                // Remover qualquer listener antigo
                for (ActionListener al : optionButtons[i].getActionListeners()) {
                    optionButtons[i].removeActionListener(al);
                }

                // Adicionar ActionListener
                final int optionIndex = i;
                optionButtons[i].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (waitingForAnswer) {
                            selectOption(optionIndex);
                        }
                    }
                });
            }

            // Preparar estado do jogo
            selectedOption = -1;
            waitingForAnswer = true;

            // Iniciar temporizador (thread separada)
            startTimer(timeLimit);
        });
    }


    private void selectOption(int optionIndex) {
        if (!waitingForAnswer) return;

        selectedOption = optionIndex;
        waitingForAnswer = false;

        // Stop timer when answer is selected
        stopTimer();

        // Highlight selected button
        for (int i = 0; i < optionButtons.length; i++) {
            if (i == optionIndex) {
                optionButtons[i].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.YELLOW, 5),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
            optionButtons[i].setEnabled(false);
        }

        System.out.println("Selected option: " + optionIndex);

        if (answerCallback != null) {
            answerCallback.onAnswerSelected(currentQuestionIndex, optionIndex);
        }
    }

    /**
     * Reset for next question
     */
    public void resetForNextQuestion() {
        selectedOption = -1;
        waitingForAnswer = false;

        for (JButton button : optionButtons) {
            button.setEnabled(false);
            button.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            button.setVisible(true);
            button.setText("");
        }

        questionLabel.setText("Waiting for next question...");
        timerLabel.setText(String.valueOf(BEGINTIME));
        timerLabel.setForeground(Color.WHITE);
    }

    /**
     * Get selected option
     */
    public int getSelectedOption() {
        return selectedOption;
    }

    private JPanel createScoreboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(250, 0));

        JLabel scoreboardTitle = new JLabel("Scoreboard", SwingConstants.CENTER);
        scoreboardTitle.setFont(new Font("Arial", Font.BOLD, 20));
        scoreboardTitle.setForeground(new Color(46, 49, 146));
        panel.add(scoreboardTitle, BorderLayout.NORTH);

        scoreboardArea = new JTextArea();
        scoreboardArea.setEditable(false);
        scoreboardArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        scoreboardArea.setText("Waiting for game\nto start...");
        scoreboardArea.setLineWrap(true);
        scoreboardArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(scoreboardArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Display scoreboard
     */
    public void displayScoreboard(String scoreboardText) {
        scoreboardArea.setText(scoreboardText);
        scoreboardArea.setCaretPosition(0); // Scroll to top
    }


    /**
     * Show game ended screen
     */
    public void showGameEnded(String finalResults) {
        stopTimer();

        // Update question label
        questionLabel.setText("<html><div style='text-align: center; color: #2e3192;'>" +
                "<h1> Game Over! </h1></div></html>");

        // Disable all buttons
        for (JButton button : optionButtons) {
            button.setEnabled(false);
            button.setText("");
            button.setVisible(false);
        }

        // Reset timer display
        timerLabel.setText("--");
        timerLabel.setForeground(Color.WHITE);

        // Show final results in scoreboard
        scoreboardArea.setText(" FINAL RESULTS \n\n" + finalResults);
        scoreboardArea.setCaretPosition(0);

        // Show dialog with final results
        new Thread(() -> {
            try {
                Thread.sleep(500); // Small delay for visual effect
                JOptionPane.showMessageDialog(
                        this,
                        finalResults,
                        " Game Over - Final Results ",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}

