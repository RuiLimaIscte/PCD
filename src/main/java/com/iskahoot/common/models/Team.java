package com.iskahoot.common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Team implements Serializable {
    private static final long serialVersionUID = 1L;

    private String teamCode;
    private List<Player> players;
    private int teamScore;
    private static final int MAX_PLAYERS = 2;

    public Team(String teamCode) {
        this.teamCode = teamCode;
        this.players = new ArrayList<>();
        this.teamScore = 0;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);  // Return copy for thread safety
    }

    public int getTeamScore() {
        return teamScore;
    }

    public void addScore(int points) {
        this.teamScore += points;
    }

    public boolean addPlayer(Player player) {
        if (players.size() >= MAX_PLAYERS) {
            return false;
        }
        players.add(player);
        return true;
    }

    public boolean isFull() {
        return players.size() >= MAX_PLAYERS;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean allPlayersAnswered() {
        return players.stream().allMatch(Player::hasAnswered);
    }

    public void resetPlayersForNextRound() {
        players.forEach(Player::resetForNextRound);
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

