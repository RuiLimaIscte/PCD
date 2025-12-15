package com.iskahoot.common.models;

import java.io.Serializable;


public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerCode;
    private String teamCode;
    private String gameCode;
    private int individualScore;

    public Player(String playerCode, String teamCode, String gameCode) {
        this.playerCode = playerCode;
        this.teamCode = teamCode;
        this.gameCode = gameCode;
        this.individualScore = 0;

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

}

