package com.iskahoot.common.messages;

import java.io.Serializable;
import java.util.Map;


public class ScoreboardData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> teamScores;  // teamCode - score
    private Map<String, Integer> lastRoundScores; // teamCode - points earned last round
    private int currentRound;
    private int totalRounds;

    public ScoreboardData(Map<String, Integer> teamScores, Map<String, Integer> lastRoundScores,
                         int currentRound, int totalRounds) {
        this.teamScores = teamScores;
        this.lastRoundScores = lastRoundScores;
        this.currentRound = currentRound;
        this.totalRounds = totalRounds;
    }

    public Map<String, Integer> getTeamScores() {
        return teamScores;
    }

    public Map<String, Integer> getLastRoundScores() {
        return lastRoundScores;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    @Override
    public String toString() {
        return "ScoreboardData{" +
                "round=" + currentRound + "/" + totalRounds +
                ", teams=" + teamScores.size() +
                '}';
    }
}

