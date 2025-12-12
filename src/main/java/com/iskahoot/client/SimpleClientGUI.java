package com.iskahoot.client;

import com.iskahoot.common.messages.QuestionMessage;
import com.iskahoot.common.messages.ScoreboardData;
import com.iskahoot.common.models.Player;
import com.iskahoot.common.models.Question;
import com.iskahoot.utils.AnswerListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

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
    private ScoreboardData lastScoreboard;

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

        setTitle("Kahoot Client - " + player.getPlayerCode() + " " + player.getTeamCode() + " " + player.getGameCode() + " " + questionMessage.getQuestionType() );
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

    public void updateScoreboard(ScoreboardData data) {
        this.lastScoreboard = data;
        SwingUtilities.invokeLater(() -> {
            StringBuilder sb = new StringBuilder();

            // Cabeçalho da Ronda
            sb.append("=== RONDA ").append(data.getCurrentRound())
                    .append("/").append(data.getTotalRounds()).append(" ===\n\n");

            // Itera sobre as equipas ordenando por pontuação total
            data.getTeamScores().entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(entry -> {
                        String team = entry.getKey();
                        int totalScore = entry.getValue();

                        // Obtém os pontos da última ronda (seguro contra nulls)
                        int lastRoundScore = data.getLastRoundScores().getOrDefault(team, 0);

                        // Formatação pedida: "Equipa: Score: X | LastRound: Y"
                        sb.append(team).append("\n")
                                .append("   Score: ").append(totalScore)
                                .append(" | LastRound: ").append(lastRoundScore)
                                .append("\n\n"); // Duplo \n para separar visualmente as equipas
                    });

            scoreboardArea.setText(sb.toString());
        });
        if (data.getCurrentRound() == data.getTotalRounds()) {
            SwingUtilities.invokeLater(this::showGameEnded);
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

        timerLabel.setText("Timer: 30"); //
        //Update title with question type
        setTitle("Kahoot Client - " + player.getPlayerCode() + " " + player.getTeamCode() + " " + player.getGameCode() + " " + questionMessage.getQuestionType() );

        System.out.println("GUI atualizada com nova pergunta.");
    }

//    //Timer update method //TODO  posso usar invoke later?
//    public static void updateTimerLabel(long timeLeft) {
//        SwingUtilities.invokeLater(() -> {
//            timerLabel.setText("Timer: " + timeLeft);
//        });
//    }

    public void startTimer(int totalSeconds) {

        // 2. Criar uma nova thread para não bloquear
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
        countdownThread.start();
    }

    public void stopTimer() {
        //Se a thread existe e está viva, interrompe
        if (countdownThread != null && countdownThread.isAlive()) {
            countdownThread.interrupt();
        }
    }

    private void handleOption(int index) {
        stopTimer();

        selectedOption = index;

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setEnabled(false);
            if (i == index) {
                optionButtons[i].setBackground(Color.GREEN);
            }
        }
        if (questionMessage != null && listener != null) {
            // O Cliente PREENCHE o campo vazio
            questionMessage.setSelectedAnswerIndex(index);
            questionMessage.setClientCode(player.getPlayerCode());
            // Envia o objeto modificado de volta via Callback
            listener.onAnswerSelected(questionMessage);
        }
        System.out.println("Selected: " + index);
    }

    private void showGameEnded() {
        stopTimer();

        SwingUtilities.invokeLater(() -> {
            // Mudar o título da janela
            setTitle("Kahoot - Resultados Finais");

            // Limpar todos os componentes da janela
            getContentPane().removeAll();
            getContentPane().setLayout(new BorderLayout());

            // Título
            JLabel titleLabel = new JLabel("Resultados Finais", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            getContentPane().add(titleLabel, BorderLayout.NORTH);

            // Garantir que a scoreboardArea existe e está só de leitura
            if (scoreboardArea == null) {
                scoreboardArea = new JTextArea();
            }
            scoreboardArea.setEditable(false);
            scoreboardArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

            // Se quiseres, aqui podes reconstruir o texto final com lastScoreboard
            // (por agora, usamos o que já lá estiver)
            if (lastScoreboard == null && scoreboardArea.getText().isEmpty()) {
                scoreboardArea.setText("Jogo terminado. Sem dados de scoreboard.");
            }

            JScrollPane scrollPane = new JScrollPane(scoreboardArea);
            getContentPane().add(scrollPane, BorderLayout.CENTER);

            // Botão de fechar
            JButton closeButton = new JButton("Fechar");
            closeButton.addActionListener(e -> dispose());

            JPanel bottomPanel = new JPanel();
            bottomPanel.add(closeButton);
            getContentPane().add(bottomPanel, BorderLayout.SOUTH);

            // Atualizar UI
            revalidate();
            repaint();
        });
    }
}
