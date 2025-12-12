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

    public int getSelectedOption() {
        return selectedOption;
    }

    private int selectedOption = -1;
    private ScoreboardData lastScoreboard;

    private Player player;


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
                    //TODO quando as equipas respondem alternadamente nao da display bem
                    // Obtém os pontos da última ronda
                    int lastRoundScore = data.getLastRoundScores().getOrDefault(team, 0);

                    sb.append(team).append("\n")
                            .append("   Score: ").append(totalScore)
                            .append(" | LastRound: ").append(lastRoundScore)
                            .append("\n\n");
                });

        scoreboardArea.setText(sb.toString());

        if (data.getCurrentRound() == data.getTotalRounds()) {
            showGameEnded();
        }
    }
    //TODO POSSIVEL RESOLUÇAO DO TODO EM CIMA SOBRE UPTADESCOREBOARD
//    public void updateScoreboard(ScoreboardData data) {
//        this.lastScoreboard = data;
//
//        StringBuilder sb = new StringBuilder();
//
//        // 1. Cabeçalho com alinhamento
//        sb.append("=== RONDA ").append(data.getCurrentRound())
//                .append("/").append(data.getTotalRounds()).append(" ===\n\n");
//
//        // Cabeçalho da Tabela
//        // %-15s = String alinhada à esquerda com 15 espaços
//        // %8s  = String alinhada à direita com 8 espaços
//        sb.append(String.format("%-15s | %8s | %8s%n", "Equipa", "Total", "Ronda"));
//        sb.append("---------------------------------------\n");
//
//        // 2. Garantir que temos todas as equipas (União das chaves, por segurança)
//        // Normalmente data.getTeamScores() tem todas, mas isto previne erros se houver inconsistência
//        var allTeams = data.getTeamScores().keySet();
//
//        // 3. Processar e Ordenar
//        allTeams.stream()
//                // Ordenar por pontuação Total (Decrescente)
//                .sorted((team1, team2) -> {
//                    int score1 = data.getTeamScores().getOrDefault(team1, 0);
//                    int score2 = data.getTeamScores().getOrDefault(team2, 0);
//                    return Integer.compare(score2, score1);
//                })
//                .forEach(team -> {
//                    // Obter pontuação total
//                    int totalScore = data.getTeamScores().getOrDefault(team, 0);
//
//                    // Obter pontuação da última ronda com segurança
//                    // Se o mapa for null ou a equipa não existir, assume 0
//                    int lastRoundScore = 0;
//                    if (data.getLastRoundScores() != null) {
//                        lastRoundScore = data.getLastRoundScores().getOrDefault(team, 0);
//                    }
//
//                    // Opção visual: Se quiser mostrar "-" quando foi 0 pontos, pode fazer um if aqui.
//                    // Mas para o Kahoot, mostrar o 0 é o comportamento normal.
//
//                    // 4. Formatar a linha
//                    sb.append(String.format("%-15s | %8d | %+8d%n",
//                            truncate(team, 15), // Garante que nomes longos não partem a tabela
//                            totalScore,
//                            lastRoundScore));   // O '+' força o sinal (ex: +50 ou +0)
//                });
//
//        scoreboardArea.setText(sb.toString());
//
//        // Verificar fim de jogo
//        if (data.getCurrentRound() >= data.getTotalRounds()) {
//            showGameEnded();
//        }
//    }
//
//    // Método auxiliar para cortar nomes muito longos para não estragar a tabela
//    private String truncate(String str, int width) {
//        if (str.length() > width) {
//            return str.substring(0, width - 3) + "...";
//        }
//        return str;
//    }

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

    public void startTimer(int totalSeconds) {

        // Criar uma nova thread para não bloquear
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
        //Se a thread existe e nao está viva, interrompe
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
        //TODO perguntar como funciona o listener
        if (questionMessage != null && listener != null) {
            // O Cliente preenche o campo vazio
            questionMessage.setSelectedAnswerIndex(index);
            questionMessage.setClientCode(player.getPlayerCode());
            // Envia o objeto modificado ao listener
            listener.onAnswerSelected(questionMessage);
        }
        System.out.println("Selected: " + index);
    }

    private void showGameEnded() {
        stopTimer();

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
    }
    private void showGameEndedPopUp() {
        stopTimer();

        // Cria uma nova janela pequena (Dialog)
        JDialog resultDialog = new JDialog(this, "Resultados Finais", true);
        resultDialog.setSize(400, 300);
        resultDialog.setLocationRelativeTo(this);

        JTextArea finalScoreArea = new JTextArea();
        finalScoreArea.setText(scoreboardArea.getText()); // Copia o texto atual
        finalScoreArea.setEditable(false);

        resultDialog.add(new JScrollPane(finalScoreArea));

        resultDialog.setVisible(true);
    }

}
