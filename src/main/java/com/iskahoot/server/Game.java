package com.iskahoot.server;

import com.iskahoot.common.models.Player;
import com.iskahoot.common.models.Quiz;
import com.iskahoot.common.models.Team;

import java.util.*;
import java.util.stream.Collectors;

import static com.iskahoot.utils.QuestionLoader.loadFromFile;


public class Game {

    private final String gameCode;
    private final Quiz quiz;
    private final int numberOfTeams;
    private final int playersPerTeam;
    private List<Team> teams = new ArrayList<>();

    public Game(String gameCode, int numberOfTeams, int playersPerTeam,int nQuestions) {
        this.gameCode = gameCode;
        this.numberOfTeams = numberOfTeams;
        this.playersPerTeam = playersPerTeam;
        this.quiz = loadFromFile("src/main/resources/questions.json");
        quiz.limitQuestions(nQuestions);
    }

    public synchronized boolean addPlayer(String teamCode, String playerCode) {
        Team team = getTeam(teamCode);

        // Se nao existe, cria e adiciona à lista
        if (team == null) {
            if(teams.size() >= numberOfTeams) {
                return false;
            }
            team = new Team(teamCode, playersPerTeam);
            teams.add(team);
        }
        if (team.isFull()) {
            return false;
        }
        Player newPlayer = new Player(playerCode, teamCode, gameCode);
        boolean added = team.addPlayer(newPlayer);

        if (added) {
            System.out.println("Adicionado " + playerCode + " à equipa " + teamCode);
            return true;
        }
        return false;
    }

    public Team getTeam(String teamCode) {
        synchronized (teams) {
            for (Team t : teams) {
                if (t.getTeamCode().equals(teamCode)) { //
                    return t;
                }
            }
        }
        return null;
    }

    public boolean isGameFull() {
        return getConnectedPlayersCount() >= getExpectedPlayers();
    }

    public List<Team> getTeams() {
        return teams;
    }
    public Map<String, Integer> getTeamScoresMap() {
        synchronized (teams) {
            return teams.stream()
                    .collect(Collectors.toMap(Team::getTeamCode, Team::getTeamScore));
        }
    }
    public String getGameCode() {
        return gameCode;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public int getExpectedPlayers() {
        return numberOfTeams * playersPerTeam;
    }

    public int getConnectedPlayersCount() {
        synchronized (teams) {
            return teams.stream().mapToInt(Team::getPlayerCount).sum(); //
        }
    }

    public Quiz getQuiz() {
        return quiz;
    }


}

