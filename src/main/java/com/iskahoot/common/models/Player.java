package com.iskahoot.common.models;

import java.io.Serializable;

/**
 * Represents a player in the game
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String teamCode;
    private int individualScore;
    private boolean hasAnswered;
    private Integer currentAnswer;  // Indice da sua resposta

    public Player(String username, String teamCode) {
        this.username = username;
        this.teamCode = teamCode;
        this.individualScore = 0;
        this.hasAnswered = false;
        this.currentAnswer = null;
    }

    public String getUsername() {
        return username;
    }

    public String getTeamCode() {
        return teamCode;
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
                "username='" + username + '\'' +
                ", team='" + teamCode + '\'' +
                ", score=" + individualScore +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return username.equals(player.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}

