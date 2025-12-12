package com.iskahoot.common.messages;

import com.iskahoot.common.models.Player;

import java.io.Serializable;

public class ConnectionMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String gameCode;
    private String teamCode;
    private String clientCode;


    public ConnectionMessage(Player player) {
        this.gameCode = player.getGameCode();
        this.teamCode = player.getTeamCode();
        this.clientCode = player.getPlayerCode();
    }
    public String getGameCode() {
        return gameCode;
    }
    public String getTeamCode() {
        return teamCode;
    }
    public String getClientCode() {
        return clientCode;
    }


}
