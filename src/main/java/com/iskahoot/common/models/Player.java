package com.iskahoot.common.models;

import java.io.Serializable;


public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerCode;
    private String teamCode;
    private String gameCode;
    private int individualScore;
    private boolean hasAnswered;
    private Integer currentAnswer;

    public Player(String playerCode, String teamCode, String gameCode) {
        this.playerCode = playerCode;
        this.teamCode = teamCode;
        this.gameCode = gameCode;
        this.individualScore = 0;
        this.hasAnswered = false;
        this.currentAnswer = null;
    }

    public String getPlayerCode() {
        return playerCode;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void addScore(int points) {
        this.individualScore += points;
    }

    public int getIndividualScore() {
        return individualScore;
    }

    public void setIndividualScore(int individualScore) {
        this.individualScore = individualScore;
    } // Stub, pús isto aqui para eventuais necessidades, mas não sei se será 100% necessário

    public boolean hasAnswered() {
        return hasAnswered;
    }

    public void setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
    }

    public Integer getCurrentAnswer() {
        return currentAnswer;
    }

    public void setCurrentAnswer(Integer currentAnswer) {
        this.currentAnswer = currentAnswer;
        this.hasAnswered = true; // Stub, faz sentido na minha cabeça, mas veremos como reage com o código.
    }

    public void resetForNextRound() {
        this.hasAnswered = false;
        this.currentAnswer = null;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + playerCode + '\'' +
                ", team='" + teamCode + '\'' +
                ", score=" + individualScore +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerCode.equals(player.playerCode);
    }

    @Override
    public int hashCode() {
        return playerCode.hashCode();
    }
}

