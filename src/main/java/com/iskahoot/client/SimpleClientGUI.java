package com.iskahoot.client;

import com.iskahoot.common.messages.QuestionMessage;
import com.iskahoot.common.models.Player;
import com.iskahoot.common.models.Question;
import com.iskahoot.utils.AnswerListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimpleClientGUI extends JFrame {

    private JLabel questionLabel;
    private JButton[] optionButtons;
    private static JLabel timerLabel;
    private JTextArea scoreboardArea;

    private Thread countdownThread;

    private QuestionMessage questionMessage;
    private AnswerListener listener;

    private List<Question> questions;
    private int currentQuestionindex = 0;

    public int getSelectedOption() {
        return selectedOption;
    }

    private int selectedOption = -1;
    // private boolean waitingForAnswer = false;


    private Player player;


//    public static void main(String[] args) throws IOException {
//        Quiz quiz = new Quiz(loadFromFile("src/main/resources/questions.json").getName(),
//                loadFromFile("src/main/resources/questions.json").getQuestions());
//
//        new SimpleClientGUI(quiz.getQuestions(), "Client 1");
//    }

    public SimpleClientGUI(QuestionMessage questionMessage, Player clientInfo, AnswerListener listener) {

        this.questionMessage = questionMessage;
        this.player = clientInfo;
        this.listener = listener;

        setTitle("Kahoot Client - " + player.getPlayerCode() + " " + player.getTeamCode() + " " + player.getGameCode() );
        setSize(700, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildGUI();

        displayQuestion(questionMessage);
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

    private void displayQuestion(QuestionMessage questionMessage) {
        questionLabel.setText(questionMessage.getQuestionText());
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(questionMessage.getOptions().get(i));
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

        timerLabel.setText("Timer: 30");

        System.out.println("GUI atualizada com nova pergunta.");
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

    public void startTimer(int totalSeconds) {

        // 2. Criar uma nova thread para NÃO bloquear a janela
        countdownThread = new Thread(() -> {
            int timeLeft = totalSeconds;

            try {
                while (timeLeft >= 0) {

                    timerLabel.setText("Timer: " + timeLeft);

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

        // 3. Iniciar a contagem
        countdownThread.start();
    }

    public void stopTimer() {
        // Se a thread existe e está viva, interrompe o sono dela
        if (countdownThread != null && countdownThread.isAlive()) {
            countdownThread.interrupt();
        }
    }

    private void handleOption(int index) {
        stopTimer();

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
        if (questionMessage != null && listener != null) {
            // 1. O Cliente PREENCHE o campo vazio
            questionMessage.setSelectedAnswerIndex(index);
            questionMessage.setClientCode(player.getPlayerCode());
            // 2. Envia o objeto modificado de volta via Callback
            listener.onAnswerSelected(questionMessage);
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
