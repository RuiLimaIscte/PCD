package com.iskahoot.server;

import com.iskahoot.common.models.Player;
import com.iskahoot.common.models.Quiz;
import com.iskahoot.common.models.Team;

import java.util.*;
import java.util.stream.Collectors;

import static com.iskahoot.utils.QuestionLoader.loadFromFile;


public class Game {
    public enum STATUS {
        WAITING,    // Waiting for players
        READY,      // All players connected
        PLAYING,    // Game in progress
        FINISHED    // Game ended
    }

    private final String gameCode;
    private final Quiz quiz;
    private final int numberOfTeams;
    private final int playersPerTeam;
    private List<Team> teams = new ArrayList<>();
    private STATUS status;

    public Game(String gameCode, int numberOfTeams, int playersPerTeam) {
        this.gameCode = gameCode;
        this.numberOfTeams = numberOfTeams;
        this.playersPerTeam = playersPerTeam;
        this.quiz = loadFromFile("src/main/resources/questions.json");
        this.status = STATUS.WAITING;
    }

    public synchronized void addPlayer(String teamCode, String playerCode) {
        Team team = getTeam(teamCode);


        // Se não existe, cria e adiciona à lista
        if (team == null) {
            team = new Team(teamCode);
            teams.add(team);
        } else {


        }

        // Adiciona o jogador (Team.java já tem lógica para não exceder MAX_PLAYERS)
        Player newPlayer = new Player(playerCode, teamCode, gameCode);
        boolean added = team.addPlayer(newPlayer);

        if (added) {
            System.out.println("Adicionado " + playerCode + " à equipa " + teamCode);
        } else {
            System.out.println("Equipa cheia! Não foi possível adicionar " + playerCode);
        }

        // Check if room is ready to start
        if (isGameFull()) {
            status = STATUS.READY;
            System.out.println("Room " + gameCode + " is ready to start!");
        }
    }

    public Team getTeam(String teamCode) {
        synchronized (teams) { // Proteção para iteração
            for (Team t : teams) {
                if (t.getTeamCode().equals(teamCode)) { //
                    return t;
                }
            }
        }
        return null;
    }

    public synchronized boolean canStartGame() {
        if (status != STATUS.READY) {
            System.out.println("Room is not ready to start, waiting for players to join...");
            return false;
        }

        status = STATUS.PLAYING;

        System.out.println("Game starting in room: " + gameCode);

        return true;
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

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Quiz getQuiz() {
        return quiz;
    }


}

