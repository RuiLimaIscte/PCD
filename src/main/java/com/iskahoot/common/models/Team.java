package com.iskahoot.common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Team implements Serializable {
    private static final long serialVersionUID = 1L;

    private String teamCode;
    private List<Player> players;
    private int teamScore;
    private int maxPlayers;

    public Team(String teamCode,int maxPlayers) {
        this.teamCode = teamCode;
        this.players = new ArrayList<>();
        this.teamScore = 0;
        this.maxPlayers = maxPlayers;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public int getTeamScore() {
        return teamScore;
    }

    public void addScore(int points) {
        this.teamScore += points;
    }

    public boolean addPlayer(Player player) {
        if (players.size() >= maxPlayers) {
            return false;
        }
        players.add(player);
        return true;
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public int getPlayerCount() {
        return players.size();
    }

    @Override
    public String toString() {
        return "Team{" +
                "code='" + teamCode + '\'' +
                ", players=" + players.size() +
                ", score=" + teamScore +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return teamCode.equals(team.teamCode);
    }

    @Override
    public int hashCode() {
        return teamCode.hashCode();
    }
}

