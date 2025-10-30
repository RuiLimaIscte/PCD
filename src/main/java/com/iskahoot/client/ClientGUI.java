package com.iskahoot.client;

import javax.swing.*;
import java.awt.*;

/**
 * GUI for the client application
 * TODO: Implement in Phase 1
 */
public class ClientGUI extends JFrame {
    private Client client;

    // GUI Components
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

//    public ClientGUI(Client client) {
//        this.client = client;
//        initializeGUI();
//    }

    public ClientGUI() {
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("IsKahoot - Quiz Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(46, 49, 146));

        // TODO: Phase 1 - Implement GUI components
        // - Question display area ///
        // - Answer buttons ///
        // - Timer display ///
        // - Scoreboard panel ///

        timerPanel = displayTimerPanel();
        mainPanel.add(timerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        // Question Panel
        questionPanel = createQuestionPanel();
        centerPanel.add(questionPanel, BorderLayout.NORTH);

        optionsPanel = createOptionsPanel();
        centerPanel.add(optionsPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Scoreboard Panel (Right) - ADICIONAR ISTO
        JPanel scoreboardPanel = createScoreboardPanel();
        mainPanel.add(scoreboardPanel, BorderLayout.EAST);


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

    /**
     * Display a question with options
     */
    public void displayQuestion(String question, String[] options, int timeLimit) {
        // Update question text
        questionLabel.setText("<html><div style='text-align: center;'>" + question + "</div></html>");

        // Update option buttons
        String[] choice = {"1", "2", "3", "4"};
        for (int i = 0; i < optionButtons.length && i < options.length; i++) {
            optionButtons[i].setText("<html><div style='text-align: center;'>" +
                    choice[i] + "<br>" + options[i] + "</div></html>");
            optionButtons[i].setEnabled(true);
            optionButtons[i].setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            optionButtons[i].setVisible(true);
        }

        // Hide extra buttons if less than 4 options
        for (int i = options.length; i < optionButtons.length; i++) {
            optionButtons[i].setVisible(false);
        }

        selectedOption = -1;
        waitingForAnswer = true;

        // Start timer
        startTimer(timeLimit);
    }

    /**
     * Handle option selection
     */
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

        // Notify client about the selection
        if (client != null) {
            // client.sendAnswer(optionIndex);
        }

        System.out.println("Selected option: " + optionIndex);
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

//    /**
//     * Set client reference
//     */
//    public void setClient(Client client) { ????????????
//        this.client = client;
//    }


    /**
     * Create scoreboard panel
     */
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
                "<h1>🏆 Game Over! 🏆</h1></div></html>");

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
        scoreboardArea.setText("=== FINAL RESULTS ===\n\n" + finalResults);
        scoreboardArea.setCaretPosition(0);

        // Show dialog with final results (optional but nice)
        new Thread(() -> {
            try {
                Thread.sleep(500); // Small delay for visual effect
                JOptionPane.showMessageDialog(
                        this,
                        finalResults,
                        "🏆 Game Over - Final Results 🏆",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws InterruptedException {
        ClientGUI gui = new ClientGUI();

        // Test sequence
        Thread.sleep(2000);

        // Question 1
        System.out.println("Question 1...");
        String[] options1 = {
                "Processo",
                "Aplicação",
                "Programa",
                "Processo Ligeiro"
        };
        gui.displayQuestion("O que é uma thread?", options1, 30);

        // Update scoreboard during question
        Thread.sleep(2000);
        gui.displayScoreboard(
                "Rank  Team           Points\n" +
                        "==============================\n" +
                        "  1.  Team Alpha      100\n" +
                        "  2.  Team Beta        80\n" +
                        "  3.  Team Gamma       60\n" +
                        "  4.  Team Delta       40\n"
        );

        // Wait for timer or answer
        Thread.sleep(16000);

        // Question 2
        System.out.println("Question 2...");
        gui.resetForNextQuestion();
        Thread.sleep(1000);

        String[] options2 = {
                "join()",
                "sleep()",
                "interrupted()",
                "wait()"
        };
        gui.displayQuestion("Qual NÃO é bloqueante?", options2, 30);

        // Update scoreboard
        Thread.sleep(2000);
        gui.displayScoreboard(
                "Rank  Team           Points\n" +
                        "==============================\n" +
                        "  1.  Team Alpha      250\n" +
                        "  2.  Team Beta       200\n" +
                        "  3.  Team Gamma      150\n" +
                        "  4.  Team Delta      100\n"
        );

        Thread.sleep(16000);

        // Show final results
        System.out.println("Game ending...");
        gui.showGameEnded(
                "🥇 1st: Team Alpha - 450 pts\n" +
                        "🥈 2nd: Team Beta - 380 pts\n" +
                        "🥉 3rd: Team Gamma - 320 pts\n" +
                        "4th: Team Delta - 280 pts\n\n" +
                        "Congratulations to all teams!"
        );
    }
}

